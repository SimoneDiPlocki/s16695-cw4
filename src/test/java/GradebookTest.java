import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.Gradebook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


/**
  * Klasa testowa dla dzienniczka ocen.
  */

public final class GradebookTest {

// --- Stale dla lintera (usuniecie bledu MagicNumber) ---
private static final double GRADE_3_0 = 3.0;
private static final double GRADE_4_0 = 4.0;
private static final double GRADE_4_5 = 4.5;
private static final double GRADE_5_0 = 5.0;
private static final double DELTA = 0.001;
private static final int EXPECTED_COUNT = 2;

/**
 * Instancja testowanego dzienniczka.
 */

    private Gradebook gradebook;

 /**
  * Inicjalizacja przed kazdym testem (czysty stan).
  */

    @BeforeEach
    public void setUp() {
        gradebook = new Gradebook();
    }

/**
 * Test dodawania przedmiotu.
 */

    @Test
    public void testAddSubject() {
        gradebook.addSubject("Physics");
        List<String> expectedList = List.of("Physics");
        assertEquals(expectedList, gradebook.getSubjects());
    }
/**
  * Test dodawania oceny z danego przedmiotu.
  */

    @Test
    public void testAddGradeToSubject() {
        gradebook.addSubject("Math");
        gradebook.addGrade("Math", GRADE_5_0);

        Map<String, List<Double>> expectedMap = new HashMap<>();
        expectedMap.put("Math", List.of(GRADE_5_0));

        assertEquals(expectedMap, gradebook.getGrades());
    }

/**
  * Testy dynamiczne dla dodawania ocen z przedmiotow.
  * @return Strumien testow dynamicznych.
  */

    @TestFactory
    Stream<DynamicTest> dynamicTestsForSubjects() {
        gradebook.addSubject("Math");
        gradebook.addSubject("Chemistry");
        gradebook.addSubject("Biology");
        gradebook.addSubject("Physics");
        gradebook.addSubject("History");

        return gradebook.getSubjects().stream()
                .map(subject -> DynamicTest.dynamicTest(
                        "Test for subject: " + subject, () -> {
                            gradebook.addGrade(subject, GRADE_4_0);
                            gradebook.addGrade(subject, GRADE_5_0);

                            assertEquals(EXPECTED_COUNT,
                                    gradebook.getGrades().get(subject).size(),
                                    "Amount of grades test: " + subject);

                            double actualAverage = gradebook.calcAvgForSubject(subject);
                            assertEquals(GRADE_4_5, actualAverage, DELTA,
                                    "Grade average test: " + subject);
                        }));
    }

/**
  * Dynamiczne testy wyliczania sredniej dla wszystkich.
  * @return Strumien testow dynamicznych.
  */

    @TestFactory
    Stream<DynamicTest> dynamicTestCalcAvgForAllSubjects() {
        record TestCase(
                String testName,
                List<String> subjectsToAdd,
                Map<String, List<Double>> gradesToAdd,
                Map<String, Double> expectedResult
        ) { }

        Stream<TestCase> testCases = Stream.of(
                new TestCase(
                        "Test 1: Pusty",
                        List.of(),
                        Map.of(),
                        Map.of()
                ),
                new TestCase(
                        "Test 2: Brak Ocen",
                        List.of("History", "Biology"),
                        Map.of(),
                        Map.of()
                ),
                new TestCase(
                        "Test 3: Poprawne obliczenie dla 3",
                        List.of("Math", "Physics", "Chemistry"),
                        Map.of(
                                "Math", List.of(GRADE_4_0, GRADE_5_0),
                                "Physics", List.of(GRADE_3_0, GRADE_3_0),
                                "Chemistry", List.of(GRADE_5_0)
                        ),
                        Map.of(
                                "Math", GRADE_4_5,
                                "Physics", GRADE_3_0,
                                "Chemistry", GRADE_5_0
                        )
                )
        );

        return testCases.map(testCase -> DynamicTest.dynamicTest(
                testCase.testName(), () -> {
                    Gradebook localGb = new Gradebook();

                    testCase.subjectsToAdd().forEach(localGb::addSubject);
                    testCase.gradesToAdd().forEach((subject, grades) -> {
                        grades.forEach(g -> localGb.addGrade(subject, g));
                    });

                    Map<String, Double> actualAverages =
                            localGb.calcAvgForAllSubjects();
                    assertEquals(testCase.expectedResult(), actualAverages,
                            "Blad srednich w: " + testCase.testName());
                }));
    }

    /**
     * Test wyjatku przy braku przedmiotu podczas oceniania.
     */
    @Test
    public void testAddGradeToNonExistentSubjectThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gradebook.addGrade("Alien Language", GRADE_5_0)
        );
        assertEquals("Alien Language not found in list of subjects",
                exception.getMessage());
    }

    /**
     * Test wyjatku przy braku przedmiotu podczas liczenia sredniej.
     */
    @Test
    public void testCalcAvgForNonExistentSubjectThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gradebook.calcAvgForSubject("Quantum Physics")
        );
        assertEquals("Subject not in subjects", exception.getMessage());
    }

    /**
     * Test wyjatku przy braku ocen dla przedmiotu (dzielenie przez zero).
     */
    @Test
    public void testCalcAvgForSubjectWithNoGradesThrowsException() {
        gradebook.addSubject("Art");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gradebook.calcAvgForSubject("Art")
        );
        assertEquals("No grades found for subject", exception.getMessage());
    }
}