package client.gui.sort;

import common.models.Semester;
import common.models.StudyGroup;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Stack;
import java.util.stream.Collectors;

public class StudyGroupUtils {
    public static SortingAndFilteringParameters parameters;
    public static Stack<StudyGroup> sortAndFilterStudyGroups(Stack<StudyGroup> studyGroups) {

        // Отфильтровать список групп
        Stack<StudyGroup> filteredStudyGroups = studyGroups.stream()
                .filter(studyGroup -> applyFilter(studyGroup, parameters))
                .collect(Collectors.toCollection(Stack::new));

        // Отсортировать список групп
        Comparator<StudyGroup> comparator = getComparator(parameters);
        if (comparator != null) {
            filteredStudyGroups.sort(comparator);
        }

        return filteredStudyGroups;
    }

    private static boolean applyFilter(StudyGroup studyGroup, SortingAndFilteringParameters parameters) {
        String filteringColumn = parameters.getFilteringColumn();
        String filteringOperation = parameters.getFilteringOperation();
        String filteringValue = parameters.getFilteringValue();

        if (filteringColumn == null || filteringOperation == null || filteringValue == null) {
            return true;
        }

        switch (filteringColumn) {
            case "id":
                long id = studyGroup.getId();
                long filterId = Integer.parseInt(filteringValue);
                return compareValues(id, filterId, filteringOperation);

            case "name":
            case "adminName":
                String name = studyGroup.getName();
                return compareValues(name, filteringValue, filteringOperation);

            case "coordX":
                Integer x = studyGroup.getCoordinates().getX();
                Integer filterX = Integer.parseInt(filteringValue);
                return compareValues(x, filterX, filteringOperation);

            case "coordY":
                double y = studyGroup.getCoordinates().getY();
                double filterY = Double.parseDouble(filteringValue);
                return compareValues(y, filterY, filteringOperation);
            case "studentsCount":
                Long studentsCount = studyGroup.getStudentsCount();
                Long filterStudentsCount = Long.parseLong(filteringValue);
                return compareValues(studentsCount, filterStudentsCount, filteringOperation);

            case "expelledStudents":
                Long expelledStudents = studyGroup.getExpelledStudents();
                Long filterExpelledStudents = Long.parseLong(filteringValue);
                return compareValues(expelledStudents, filterExpelledStudents, filteringOperation);

            case "transferredStudents":
                Integer transferredStudents = studyGroup.getTransferredStudents();
                Integer filterTransferredStudents = Integer.parseInt(filteringValue);
                return compareValues(transferredStudents, filterTransferredStudents, filteringOperation);

            case "semesterEnum":
                Semester semester = studyGroup.getSemesterEnum();
                Semester filterSemester = Semester.valueOf(filteringValue);
                return compareValues(semester, filterSemester, filteringOperation);


            case "birthday":
                LocalDateTime birthday = studyGroup.getGroupAdmin().getBirthday();
                LocalDateTime filterBirthday = LocalDateTime.parse(filteringValue+"T00:00:00");
                return compareValues(birthday, filterBirthday, filteringOperation);

            case "weight":
                double height = studyGroup.getGroupAdmin().getWeight();
                double filterHeight = Double.parseDouble(filteringValue);
                return compareValues(height, filterHeight, filteringOperation);

            case "locX":
                Float locX = studyGroup.getGroupAdmin().getLocation().getX();
                Float filterLocX = Float.parseFloat(filteringValue);
                return compareValues(locX, filterLocX, filteringOperation);

            case "locY":
                Integer locY = studyGroup.getGroupAdmin().getLocation().getY();
                Integer filterLocY = Integer.parseInt(filteringValue);
                return compareValues(locY, filterLocY, filteringOperation);

            case "locZ":
                double locZ = studyGroup.getGroupAdmin().getLocation().getZ();
                double filterLocZ = Double.parseDouble(filteringValue);
                return compareValues(locZ, filterLocZ, filteringOperation);

            default:
                return true;
        }
    }

    private static Comparator<StudyGroup> getComparator(SortingAndFilteringParameters parameters) {
        String sortingColumn = parameters.getSortingColumn();
        boolean ascending = parameters.getAscending();

        if (sortingColumn == null) {
            return null;
        }

        Comparator<StudyGroup> comparator;
        switch (sortingColumn) {
            case "id":
                comparator = Comparator.comparing(StudyGroup::getId);
                break;

            case "name":
                comparator = Comparator.comparing(StudyGroup::getName);
                break;

            case "coordX":
                comparator = Comparator.comparingInt(s -> s.getCoordinates().getX());
                break;

            case "coordY":
                comparator = Comparator.comparing(s -> s.getCoordinates().getY());
                break;

            case "creationDate":
                comparator = Comparator.comparing(StudyGroup::getCreationDate);
                break;

            case "studentsCount":
                comparator = Comparator.comparingLong(StudyGroup::getStudentsCount);
                break;
            case "expelledStudents":
                comparator = Comparator.comparingLong(StudyGroup::getExpelledStudents);
                break;
            case "transferredStudents":
                comparator = Comparator.comparingInt(StudyGroup::getTransferredStudents);
                break;

            case "semesterEnum":
                comparator = Comparator.comparing(StudyGroup::getSemesterEnum);
                break;

            case "adminName":
                comparator = Comparator.comparing(s -> s.getGroupAdmin().getName());
                break;
            case "birthday":
                comparator = Comparator.comparing(s -> s.getGroupAdmin().getBirthday());
                break;

            case "weight":
                comparator = Comparator.comparing(s -> s.getGroupAdmin().getWeight());
                break;

            case "locX":
                comparator = Comparator.comparing(s -> s.getGroupAdmin().getLocation().getX());
                break;

            case "locY":
                comparator = Comparator.comparingInt(s -> s.getGroupAdmin().getLocation().getY());
                break;

            case "locZ":
                comparator = Comparator.comparing(s -> s.getGroupAdmin().getLocation().getZ());
                break;

            default:
                comparator = null;
        }

        return ascending ? comparator : comparator.reversed();
    }
    private static <T extends Comparable<T>> boolean compareValues(T value1, T value2, String operation) {
        int comparisonResult = value1.compareTo(value2);
        switch (operation) {
            case "=":
                return comparisonResult == 0;
            case "<":
                return comparisonResult < 0;
            case ">":
                return comparisonResult > 0;
            case ">=":
                return comparisonResult >= 0;
            case "<=":
                return comparisonResult <= 0;
            default:
                return false;
        }
    }
}
