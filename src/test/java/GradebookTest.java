import static org.junit.jupiter.api.Assertions.*;

import org.example.Gradebook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class GradebookTest {

    /*
     * BŁĄD #1: Zanieczyszczenie stanu dzienniczka.
     * Przyczyna: Użyto `private static Gradebook gradebook` oraz `@BeforeAll public static void setUp()`.
     * To powodowało, że jeden dzienniczek był współdzielony przez wszystkie testy, więc dodawane
     * w nich przedmioty i oceny nakładały się na siebie, powodując błędy asercji w kolejnych testach.
     * * Rozwiązanie: Usunięto modyfikator 'static' ze zmiennej i metody oraz zmieniono adnotację
     * na `@BeforeEach`. Dzięki temu każdy test pracuje na nowym, czystym obiekcie.
     */
    private Gradebook gradebook;

    @BeforeEach
    public void setUp() {
        gradebook = new Gradebook();
    }

    @Test
    public void testAddSubject() {
        gradebook.addSubject("Physics");
        List<String> expectedList = List.of("Physics");
        assertEquals(expectedList, gradebook.getSubjects());
    }

    /*
     * BŁĄD #2 (w klasie Gradebook.java w metodzie addGrade): Dyskryminacja Historii :)
     * Przyczyna: W metodzie addGrade był zagnieżdżony warunek `if (!subject.equals("History"))`,
     * który całkowicie blokował możliwość dodania oceny z przedmiotu History, przez co
     * testy dynamiczne nie przechodziły (oczekiwano 2 ocen, a było 0).
     * Rozwiązanie: Usunięto ten warunek z pliku Gradebook.java, pozwalając na swobodne
     * przypisywanie ocen z historii ( instrukcja to teraz po prostu: subjectsGrades.get(subject).add(grade); )
     */
    @Test
    public void testAddGradeToSubject() {
        gradebook.addSubject("Math");
        gradebook.addGrade("Math", 5.0);

        Map<String, List<Double>> expectedMap = new HashMap<>();
        expectedMap.put("Math", List.of(5.0));

        assertEquals(expectedMap, gradebook.getGrades());
    }

    /*
     * BŁĄD #3: Brak implementacji logiki w teście dynamicznym.
     * Przyczyna: W kodzie źródłowym z zadania metoda dynamicTestsForSubjects() zawierała
     * tylko zmienne ustawione na sztywno na 0.0, bez faktycznego wywołania metod dzienniczka.
     * * Rozwiązanie: Zaimplementowano logikę testu - dodanie ocen `4.0` i `5.0` do dzienniczka,
     * uaktualnienie oczekiwanych asercji ilości ocen na `2` i wyliczonej średniej na `4.5`.
     */
    @TestFactory
    Stream<DynamicTest> dynamicTestsForSubjects() {
        gradebook.addSubject("Math");
        gradebook.addSubject("Chemistry");
        gradebook.addSubject("Biology");
        gradebook.addSubject("Physics");
        gradebook.addSubject("History");

        return gradebook.getSubjects().stream()
                .map(subject -> DynamicTest.dynamicTest("Test for subject: " + subject, () -> {
                    // Dodano logikę do testowania:
                    gradebook.addGrade(subject, 4.0);
                    gradebook.addGrade(subject, 5.0);

                    int expectedAmountOfGrades = 2; // Oczekujemy dwóch ocen
                    assertEquals(expectedAmountOfGrades, gradebook.getGrades().get(subject).size(),
                            "Test amount of grades for subject: ".formatted(subject));

                    double actualAverage = gradebook.calcAvgForSubject(subject);
                    double expectedAverage = 4.5; // (4.0+5.0)/2
                    assertEquals(expectedAverage, actualAverage, 0.001,
                            "Grade average test for subject: ".formatted(subject));
                }));
    }

    // --- ZADANIE 2: Punkt 3 (Test dynamiczny metody z wyliczaniem średniej dla wszystkich przedmiotów) ---
    @TestFactory
    Stream<DynamicTest> dynamicTestCalcAvgForAllSubjects() {
        // Record pomagający uporządkować dane testowe dla różnych scenariuszy
        record TestCase(
                String testName,
                List<String> subjectsToAdd,
                Map<String, List<Double>> gradesToAdd,
                Map<String, Double> expectedResult
        ) {}

        // Zestawienie 3 różnych sytuacji, w jakich może znaleźć się dzienniczek
        Stream<TestCase> testCases = Stream.of(
                new TestCase(
                        "Test 1: Pusty dzienniczek zwraca pustą listę",
                        List.of(),
                        Map.of(),
                        Map.of()
                ),
                new TestCase(
                        "Test 2: Dwa przedmioty, w obydwu brak ocen (pusta mapa wyników)",
                        List.of("History", "Biology"),
                        Map.of(),
                        Map.of()
                ),
                new TestCase(
                        "Test 3: Poprawne obliczenie średniej po dodaniu wielu ocen do trzech przedmiotów",
                        List.of("Math", "Physics", "Chemistry"),
                        Map.of(
                                "Math", List.of(4.0, 5.0),
                                "Physics", List.of(3.0, 3.0),
                                "Chemistry", List.of(5.0)
                        ),
                        Map.of(
                                "Math", 4.5,
                                "Physics", 3.0,
                                "Chemistry", 5.0
                        )
                )
        );

        // Transformacja danych w wykonywalne testy (DynamicTest).
        // Za pomocą metody .map() przechodzimy przez każdy przygotowany scenariusz (testCase).
        // Dla każdego z nich JUnit dynamicznie wygeneruje i uruchomi osobny test.
        return testCases.map(testCase -> DynamicTest.dynamicTest(testCase.testName(), () -> {
            Gradebook localGradebook = new Gradebook();

            testCase.subjectsToAdd().forEach(localGradebook::addSubject);
            testCase.gradesToAdd().forEach((subject, grades) -> {
                grades.forEach(grade -> localGradebook.addGrade(subject, grade));
            });

            Map<String, Double> actualAverages = localGradebook.calcAvgForAllSubjects();
            assertEquals(testCase.expectedResult(), actualAverages,
                    "Niezgodność średnich ocen dla: " + testCase.testName());
        }));
    }

    /*
     * Dodatkowy Test 1: Próba dodania oceny do przedmiotu, którego nie ma w dzienniczku.
     * Oczekujemy rzucenia wyjątku IllegalArgumentException z odpowiednią wiadomością.
     */
    @Test
    public void testAddGradeToNonExistentSubjectThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gradebook.addGrade("Alien Language", 5.0)
        );
        assertEquals("Alien Language not found in list of subjects", exception.getMessage());
    }

    /*
     * Dodatkowy Test 2: Próba wyliczenia średniej dla przedmiotu, którego nie ma w dzienniczku.
     * Oczekujemy rzucenia wyjątku IllegalArgumentException.
     */
    @Test
    public void testCalcAvgForNonExistentSubjectThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gradebook.calcAvgForSubject("Quantum Physics")
        );
        assertEquals("Subject not in subjects", exception.getMessage());
    }

    /*
     * Dodatkowy Test 3: Próba wyliczenia średniej dla przedmiotu, który istnieje,
     * ale nie ma wpisanej jeszcze żadnej oceny.
     * Oczekujemy zabezpieczenia przed dzieleniem przez zero i rzucenia wyjątku.
     */
    @Test
    public void testCalcAvgForSubjectWithNoGradesThrowsException() {
        // Dodajemy przedmiot, ale NIE dodajemy do niego ocen
        gradebook.addSubject("Art");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gradebook.calcAvgForSubject("Art")
        );
        assertEquals("No grades found for subject", exception.getMessage());
    }
}