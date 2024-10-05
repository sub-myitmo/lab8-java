package client.helpers;

import common.exceptions.IncorrectInputException;
import common.exceptions.WrongCommandArgsException;
import common.models.Coordinates;
import common.models.Location;
import common.models.Person;
import common.models.Semester;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

public class CommunicationControl {

    public CommunicationControl() {
    }

    public String setName(String name) {
        try {
            if (name.isEmpty()) throw new IncorrectInputException("Имя не может быть пустым");
        } catch (IncorrectInputException e) {
            JOptionPane.showMessageDialog(null, "Неверное имя");
        }
        return name;
    }


    public Coordinates setCoordinates(String coordx, String coordy){
        Integer x;
        double y;
        try {
            if (coordx.isEmpty()) throw new IncorrectInputException();
            x = Integer.parseInt(coordx);
            if (x < 0 || x > 265) throw new IncorrectInputException();


            if (coordy.isEmpty()) throw new IncorrectInputException();
            y = Double.parseDouble(coordy);
            if (y < 0) throw new IncorrectInputException();

            return new Coordinates(x, y);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Неверный формат координат");
        }
        return null;
    }

    public Long setStudentCount(String line) {
        Long studentCount;
        try {
            if (line.isEmpty()) throw new IncorrectInputException();
            studentCount = Long.parseLong(line);
            if (studentCount < 0) throw new IncorrectInputException();
            return studentCount;
        } catch (IncorrectInputException e) {
            JOptionPane.showMessageDialog(null, "Некорректный ввод (кол-во студентов >= 0)");
        }

        return null;
    }

    public Long setExpelledStudents(String line) {
        Long expelledStudents;
        try {
            if (line.isEmpty()) throw new IncorrectInputException();
            expelledStudents = Long.parseLong(line);
            if (expelledStudents < 0) throw new IncorrectInputException();
            return expelledStudents;
        } catch (IncorrectInputException e) {
            JOptionPane.showMessageDialog(null, "Некорректный ввод (кол-во студентов >= 0)");
        }

        return null;
    }

    public Integer setTransferredStudents(String line) {
        Integer transferredStudents;
        try {
            if (line.isEmpty()) throw new IncorrectInputException();
            transferredStudents = Integer.parseInt(line);
            if (transferredStudents < 0) throw new IncorrectInputException();
            return transferredStudents;
        } catch (IncorrectInputException e) {
            JOptionPane.showMessageDialog(null, "Некорректный ввод (кол-во студентов >= 0)");
        }

        return null;
    }


    public Person setPerson(String strName, String birthday, String strWeight, Location location) {
        try {
            String name = setName(strName);
            LocalDateTime bDay = setBirthday(birthday);
            double weight = setWeight(strWeight);
            return new Person(name, bDay, weight, location);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Неверные данные Person");
        }
        return null;
    }

    public Location setLocation(String locx, String locy, String locz){
        Float x;
        Integer y;
        double z;
        try {
            if (locx.isEmpty()) throw new IncorrectInputException();
            x = Float.parseFloat(locx);


            if (locy.isEmpty()) throw new IncorrectInputException();
            y = Integer.parseInt(locy);


            if (locz.isEmpty()) throw new IncorrectInputException();
            z = Double.parseDouble(locz);

            return new Location(x, y, z);
        } catch (IncorrectInputException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Неверный формат локации");
        }
        return null;
    }

    private double setWeight(String line) {
        try {
            double weight = Long.parseLong(line);
            if ((weight <= 0)) {
                throw new WrongCommandArgsException();
            }
            return weight;
        } catch (Exception e) {
            System.out.println("Некорректный ввод. Попробуйте еще раз.");
        }
        return 0;
    }




    public LocalDateTime setBirthday(String birthdayStr) {
        try {
            if (birthdayStr.isEmpty()) throw new IllegalArgumentException();
            birthdayStr = birthdayStr.trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime bd = LocalDate.parse(birthdayStr, formatter).atStartOfDay();
            if (bd.isAfter(LocalDate.now().atStartOfDay())) throw new WrongCommandArgsException();
            return bd;
        } catch (WrongCommandArgsException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return null;
    }

    public Semester chooseSemester(String setSem) {
        Semester semester;
        try {
            semester = Semester.valueOf(setSem.toUpperCase());
            return semester;
        } catch (NoSuchElementException e) {
            JOptionPane.showMessageDialog(null, "Нет такого элемента");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка данных");
        }
        return null;
    }




}
