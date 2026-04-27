package org.example;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Gradebook gradebook = new Gradebook();

        gradebook.addSubject("Math");
        gradebook.addSubject("Physics");
        gradebook.addSubject("History");

        gradebook.addGrade("Math", 5.0);
        gradebook.addGrade("Math", 4.0);
        gradebook.addGrade("Math", 3.5);

        gradebook.addGrade("Physics", 4.5);
        gradebook.addGrade("Physics", 5.0);

        gradebook.addGrade("History", 3.0);
        gradebook.addGrade("History", 4.0);
        gradebook.addGrade("History", 5.0);

        System.out.println("=== Gradebook ===");
        System.out.println("Subjects: " + gradebook.getSubjects());
        System.out.println();

        for (String subject : gradebook.getSubjects()) {
            System.out.println(subject + " grades:  " + gradebook.getGrades().get(subject));
            System.out.println(subject + " average: " + gradebook.calcAvgForSubject(subject));
            System.out.println();
        }

        Map<String, Double> allAverages = gradebook.calcAvgForAllSubjects();
        System.out.println("All averages: " + allAverages);
    }
}

