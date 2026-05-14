package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa reprezentujaca dzienniczek ocen.
 */
public final class Gradebook {

    /** Mnoznik uzywany do zaokraglania sredniej. */
    private static final double ROUNDING_MULTIPLIER = 100.0;

    /** Lista przedmiotow w dzienniczku. */
    private final List<String> subjects;

    /** Mapa przechowujaca oceny dla danych przedmiotow. */
    private final Map<String, List<Double>> subjectsGrades;

    /**
     * Konstruktor inicjalizujacy puste struktury danych.
     */
    public Gradebook() {
        this.subjects = new ArrayList<>();
        this.subjectsGrades = new HashMap<>();
    }

    /**
     * Dodaje nowy przedmiot do dzienniczka.
     * @param subject Nazwa przedmiotu.
     */
    public void addSubject(final String subject) {
        subjects.add(subject);
        subjectsGrades.put(subject, new ArrayList<>());
    }

    /**
     * Dodaje ocene do wybranego przedmiotu.
     * @param subject Nazwa przedmiotu.
     * @param grade   Wartosc oceny.
     */
    public void addGrade(final String subject, final double grade) {
        if (subjectsGrades.containsKey(subject)) {
            subjectsGrades.get(subject).add(grade);
        } else {
            throw new IllegalArgumentException(
                    subject + " not found in list of subjects");
        }
    }

    /**
     * Wylicza srednia ocen dla podanego przedmiotu.
     * @param subject Nazwa przedmiotu.
     * @return Srednia ocen zaokraglona do 2 miejsc po przecinku.
     */
    public double calcAvgForSubject(final String subject) {
        if (subjectsGrades.containsKey(subject)) {
            List<Double> grades = subjectsGrades.get(subject);
            if (grades == null || grades.isEmpty()) {
                throw new IllegalArgumentException(
                        "No grades found for subject");
            }
            double subjectGradeSum = grades.stream()
                    .mapToDouble(Double::doubleValue).sum();
            int subjectGradeCount = grades.size();

            return Math.round((subjectGradeSum / subjectGradeCount)
                    * ROUNDING_MULTIPLIER) / ROUNDING_MULTIPLIER;
        } else {
            throw new IllegalArgumentException("Subject not in subjects");
        }
    }

    /**
     * Wylicza srednia ocen dla wszystkich przedmiotow.
     * @return Mapa zawierajaca nazwe przedmiotu i jego srednia.
     */
    public Map<String, Double> calcAvgForAllSubjects() {
        Map<String, Double> allAverages = new HashMap<>();

        for (String subject : subjects) {
            try {
                double avg = calcAvgForSubject(subject);
                allAverages.put(subject, avg);
            } catch (IllegalArgumentException e) {
                // Ignorujemy przedmioty bez ocen (zgodnie z testami).
            }
        }

        return allAverages;
    }

    /**
     * Zwraca liste wszystkich przedmiotow.
     * @return Lista przedmiotow.
     */
    public List<String> getSubjects() {
        return subjects;
    }

    /**
     * Zwraca mape z ocenami dla poszczegolnych przedmiotow.
     * @return Mapa ocen.
     */
    public Map<String, List<Double>> getGrades() {
        return subjectsGrades;
    }
}