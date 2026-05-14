package org.example;

import java.util.Map;

/**
 * Glowna klasa uruchomieniowa aplikacji.
 */
public final class Main {

    /** Stala reprezentujaca ocene 3.0. */
    private static final double GRADE_3_0 = 3.0;
    /** Stala reprezentujaca ocene 3.5. */
    private static final double GRADE_3_5 = 3.5;
    /** Stala reprezentujaca ocene 4.0. */
    private static final double GRADE_4_0 = 4.0;
    /** Stala reprezentujaca ocene 4.5. */
    private static final double GRADE_4_5 = 4.5;
    /** Stala reprezentujaca ocene 5.0. */
    private static final double GRADE_5_0 = 5.0;

    /**
     * Prywatny konstruktor ukrywajacy domyslny konstruktor publiczny.
     * Wymagane przez linter dla klas zawierajacych tylko metody statyczne.
     */
    private Main() {
    }

    /**
     * Glowna metoda programu.
     *
     * @param args Argumenty wiersza polecen.
     */
    public static void main(final String[] args) {
        Gradebook gradebook = new Gradebook();

        gradebook.addSubject("Math");
        gradebook.addSubject("Physics");
        gradebook.addSubject("History");

        gradebook.addGrade("Math", GRADE_5_0);
        gradebook.addGrade("Math", GRADE_4_0);
        gradebook.addGrade("Math", GRADE_3_5);

        gradebook.addGrade("Physics", GRADE_4_5);
        gradebook.addGrade("Physics", GRADE_5_0);

        gradebook.addGrade("History", GRADE_3_0);
        gradebook.addGrade("History", GRADE_4_0);
        gradebook.addGrade("History", GRADE_5_0);

        System.out.println("=== Gradebook ===");
        System.out.println("Subjects: " + gradebook.getSubjects());
        System.out.println();

        for (String subject : gradebook.getSubjects()) {
            System.out.println(subject + " grades:  "
                    + gradebook.getGrades().get(subject));
            System.out.println(subject + " average: "
                    + gradebook.calcAvgForSubject(subject));
            System.out.println();
        }

        Map<String, Double> allAverages = gradebook.calcAvgForAllSubjects();
        System.out.println("All averages: " + allAverages);
    }
}
