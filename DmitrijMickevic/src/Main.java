import lt.techin.municipality.*;
import lt.techin.municipality.test.AbstractMunicipalityTest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class Main extends AbstractMunicipalityTest implements Municipality {


    ArrayList<Person> arr=new ArrayList<>();

    @Override
    public void registerCitizen(Person person) throws IllegalCitizenException {

        LocalDate currentDate=LocalDate.now();
        LocalDate birthdate=person.getDateOfBirth();

        if ( person.getFirstName()==null || person.getFirstName().isEmpty() ||person.getLastName()==null || person.getLastName().isEmpty() || person.getYearlyIncome() < 0 || birthdate==null || birthdate.isAfter(currentDate)) {
            throw new IllegalCitizenException(person);

        }
        if (!arr.contains(person)) {
            arr.add(person);
        }
    }

    @Override
    public int getCitizenCount() {

            return arr.size();
    }

    @Override
    public double getAverageCitizenIncome() {
        if (arr.isEmpty()) {
            return 0;
        }

        double totalIncome = 0;
        for (Person person : arr) {
            totalIncome += person.getYearlyIncome();
        }

        return totalIncome / arr.size();
    }

    @Override
    public boolean isRegisteredCitizen(Person person) {

            return arr.contains(person);
    }

    @Override
    public Person findOldestPerson() {
        if (arr.isEmpty()) {
            return null;
        }

        Person oldest = arr.get(0);
        for (Person person : arr) {
            if (person.getDateOfBirth().isBefore(oldest.getDateOfBirth())) {
                oldest = person;
            }
        }
        return oldest;
    }

    @Override
    public int countAdultCitizens() {
        int count = 0;
        for (Person person : arr) {
            int age = calculateAge(person.getDateOfBirth());
            if (age >= 18) {
                count++;
            }
        }
        return count;
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    @Override
    public double totalIncomeInTaxes() {
        return 0;
    }

    @Override
    public Collection<Person> findCitizensBy(PersonPredicate personPredicate) {
        List<Person> filteredCitizens = new ArrayList<>();

        for (Person person : arr) {
            if (personPredicate.test(person)) {
                filteredCitizens.add(person);
            }
        }

        return filteredCitizens;
    }

    @Override
    public Collection<Person> getOrderedCitizens() {

        List<Person> orderedCitizens = new ArrayList<>(arr);

        orderedCitizens.sort(Comparator.comparing(Person::getLastName).thenComparing(Person::getFirstName));

        return orderedCitizens;
    }

    @Override
    public Map<Integer, List<Person>> groupByYearOfBirth() {

        return arr.stream()
                .collect(Collectors.groupingBy(person -> person.getDateOfBirth().getYear()));
    }

    @Override
    public Municipality getMunicipality(TaxCalculator taxCalculator) {
        return new Main();
    }

    @Override
    public TaxCalculator getTaxCalculator(TaxRateProvider taxRateProvider) {

        return person -> {
            double taxRate = taxRateProvider.getTaxRate(person.getYearlyIncome());
            return person.getYearlyIncome() * taxRate;
        };
    }

}