package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gradebook {
    private final List<String> subjects;
    private final Map<String, List<Double>> subjectsGrades;

    public Gradebook() {
        this.subjects = new ArrayList<>();
        this.subjectsGrades = new HashMap<>();
    }

    public void addSubject(String subject) {
        subjects.add(subject);
        subjectsGrades.put(subject, new ArrayList<>());
    }

    public void addGrade(String subject, double grade) {
        if (subjectsGrades.containsKey(subject)) {
            // POPRAWKA BŁĘDU 1: Usunięto złośliwy warunek blokujący dodawanie ocen z historii.
            // Zostało samo proste dodanie oceny do listy.
            subjectsGrades.get(subject).add(grade);
        } else {
            throw new IllegalArgumentException(subject + " not found in list of subjects");
        }
    }

    public double calcAvgForSubject(String subject) {
        if (subjectsGrades.containsKey(subject)) {
            List<Double> grades = subjectsGrades.get(subject);
            // Dodatkowe zabezpieczenie: jeśli lista jest pusta, rzuć wyjątek (żeby nie dzielić przez 0)
            if (grades == null || grades.isEmpty()) {
                throw new IllegalArgumentException("No grades found for subject");
            }
            double subjectGradeSum = grades.stream().mapToDouble(Double::doubleValue).sum();
            int subjectGradeCount = grades.size();
            return Math.round((subjectGradeSum / subjectGradeCount) * 100.0) / 100.0;
        } else {
            throw new IllegalArgumentException("Subject not in subjects");
        }
    }

    // ROZWIĄZANIE ZADANIA 2: Implementacja wyliczania średniej dla wszystkich przedmiotów
    public Map<String, Double> calcAvgForAllSubjects() {
        Map<String, Double> allAverages = new HashMap<>();

        for (String subject : subjects) {
            try {
                // Wykorzystujemy istniejącą już metodę, żeby nie pisać logiki dwa razy
                double avg = calcAvgForSubject(subject);
                allAverages.put(subject, avg);
            } catch (IllegalArgumentException e) {
                // Jeśli przedmiot nie ma jeszcze ocen, metoda calcAvgForSubject rzuci wyjątek.
                // My go przechwytujemy (ignorujemy) - w takim wypadku po prostu nie dodajemy tego
                // przedmiotu do naszej mapy ze średnimi (zgodnie z naszymi testami).
            }
        }

        return allAverages;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public Map<String, List<Double>> getGrades() {
        return subjectsGrades;
    }
}