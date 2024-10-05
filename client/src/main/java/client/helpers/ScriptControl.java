package client.helpers;

import common.actions.*;
import common.exceptions.*;
import common.models.Coordinates;
import common.models.Location;
import common.models.Person;
import common.models.Semester;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class ScriptControl {
    private Scanner userScanner;
    private Stack<File> scriptStack = new Stack<>();
    private Stack<Scanner> scannerStack = new Stack<>();

    public ScriptControl(File script) {
        try {
            userScanner = new Scanner(script);
            scannerStack.add(userScanner);
            scriptStack.add(script);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No file on such path found!");
        }
    }

    public Request handle(ResponseCode responseCode, User user) {
        String userInput;
        String[] userCommand;
        StatusOfCode processingCode;

        try {
            do {
                try {
                    if (responseCode == ResponseCode.ERROR)
                        throw new IncorrectScriptException();
                    while (!scannerStack.isEmpty() && !userScanner.hasNextLine()) {
                        userScanner.close();
                        userScanner = scannerStack.pop();
                        if (!scannerStack.isEmpty()) scriptStack.pop();
                        else return null;
                    }
                    userInput = userScanner.nextLine();
                    if (!userInput.isEmpty()) {
                        Console.println(userInput);
                    }
                    userCommand = (userInput.trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();


                } catch (NoSuchElementException | IllegalStateException exception) {
                    exception.printStackTrace();
                    Console.printerror("CommandErrorException");
                    userCommand = new String[]{"", ""};
                    System.exit(0);
                }
                processingCode = processCommand(userCommand[0], userCommand[1], user);
            } while (userCommand[0].isEmpty());
            try {
                if (responseCode == responseCode.ERROR || processingCode == StatusOfCode.ERROR)
                    throw new IncorrectScriptException();
                switch (processingCode) {
                    case OBJECT:
                        GroupMask mask = generateGroupAdd();
                        return new Request(userCommand[0], userCommand[1], mask, user);
                    case SCRIPT:
                        File scriptFile = new File(userCommand[1]);
                        if (!scriptFile.exists()) throw new FileNotFoundException();
                        if (!scriptStack.isEmpty() && scriptStack.search(scriptFile) != -1)
                            throw new ScriptRecursionException();
                        scannerStack.push(userScanner);
                        scriptStack.push(scriptFile);
                        userScanner = new Scanner(scriptFile);
                        Console.println("ScriptRunning " + scriptFile.getName());
                        break;
                }
            } catch (FileNotFoundException exception) {
                Console.printerror("ScriptFileNotFoundException");
                throw new IncorrectScriptException();
            } catch (ScriptRecursionException exception) {
                Console.printerror("ScriptRecursionException");
                throw new IncorrectScriptException();
            } catch (InputException e) {
                throw new RuntimeException(e);
            }
        } catch (IncorrectScriptException exception) {
            JOptionPane.showMessageDialog(null, "Некорректные данные в скрипте!");
            while (!scannerStack.isEmpty()) {
                userScanner.close();
                userScanner = scannerStack.pop();
            }
            scriptStack.clear();
            return null;
        }
        return new Request(userCommand[0], userCommand[1], null, user);
    }

    private StatusOfCode processCommand(String command, String commandArgument, User user) {
        try {

            switch (command) {
                case "":
                    return StatusOfCode.ERROR;
                case "add":
                    if (!commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    return StatusOfCode.OBJECT;
                case "clear":
                    if (!commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    break;
                case "execute_script":
                    if (commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    return StatusOfCode.SCRIPT;
                case "print_ascending":
                    if (commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    break;
                case "info":
                    if (!commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    break;
                case "print_field_ascending_students_count":
                    if (!commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    break;
                case "remove_by_id":
                    if (commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    break;
                case "shuffle":
                    if (!commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    break;
                case "reorder":
                    if (!commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    break;
                case "update_by_id":
                    if (commandArgument.isEmpty()) throw new WrongCommandArgsException();
                    return StatusOfCode.OBJECT;
                default:
                    Console.println("Команда '" + command + "' не найдена. Наберите 'help' для справки.");
                    return StatusOfCode.ERROR;
            }
        } catch (WrongCommandArgsException e) {
            System.out.println("Неправильное использование команды " + command);
            return StatusOfCode.ERROR;
        }
        return StatusOfCode.OK;
    }

    private GroupMask generateGroupAdd() throws InputException {

        return new GroupMask(
                setName(),
                setCoordinates(),
                setStudentsCount(),
                setExpelledStudents(),
                setTransferredStudents(),
                chooseSemester(),
                setPerson()
        );
    }

    private String setName() {
        String name;
        while (true) {
            try {
                name = userScanner.nextLine().trim();
                if (name.isEmpty()) throw new IncorrectInputException();
                System.out.println(name);
                return name;
            } catch (IncorrectInputException e) {
                System.out.println("name is not correct!");
            }
        }
    }

    public Coordinates setCoordinates() throws InputException {
        try {
            Integer x;
            double y;
            x = setCoodrinateX();
            y = setCoodrinateY();

            return new Coordinates(x, y);
        } catch (InputException e) {
            throw new InputException();

        }

    }

    public Integer setCoodrinateX() throws InputException {
        Integer coordX;
        String line;
        while (true) {
            try {
                line = userScanner.nextLine().trim();
                if (line.isEmpty()) throw new InputException();
                coordX = Integer.parseInt(line);
                if (coordX > 265) throw new InputException();
                return coordX;
            } catch (InputException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("должно быть числом а еще и целым !!!");
            }

        }

    }

    public double setCoodrinateY() throws InputException {
        double coordY;
        String line;
        while (true) {
            try {
                line = userScanner.nextLine().trim();
                if (line.isEmpty()) throw new InputException();
                coordY = Double.parseDouble(line);
                return coordY;
            } catch (InputException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("должно быть числом а еще и double !!!");
            }

        }

    }

    public Long setExpelledStudents() throws InputException {
        Long expelledStudents;
        String line;
        while (true) {
            try {
                line = userScanner.nextLine().trim();
                if (line.isEmpty()) throw new InputException();
                expelledStudents = Long.parseLong(line);
                if (expelledStudents <= 0) throw new InputException();
                return expelledStudents;
            } catch (InputException e) {
                throw new InputException();
            } catch (NumberFormatException e) {
                System.out.println("должно быть числом а еще и целым !!!");
            }

        }

    }

    public Long setStudentsCount() throws InputException {
        Long studentsCount;
        String line;
        while (true) {
            try {
                line = userScanner.nextLine().trim();
                if (line.isEmpty()) throw new InputException();
                studentsCount = Long.parseLong(line);
                if (studentsCount <= 0) throw new InputException();
                return studentsCount;
            } catch (InputException e) {
                throw new InputException();
            } catch (NumberFormatException e) {
                System.out.println("должно быть числом а еще и целым !!!");
            }

        }

    }


    public Integer setTransferredStudents() throws InputException {
        Integer transferredStudents;
        String line;
        while (true) {
            try {
                line = userScanner.nextLine().trim();
                if (line.isEmpty()) throw new InputException();
                transferredStudents = Integer.parseInt(line);
                if (transferredStudents <= 0) throw new InputException();
                return transferredStudents;
            } catch (InputException e) {
                throw new InputException();
            } catch (NumberFormatException e) {
                System.out.println("должно быть числом а еще и целым !!!");
            }

        }

    }


    public Semester chooseSemester() {
        Semester semester;
        String setSem;
        while (true) {
            try {

                setSem = userScanner.nextLine().trim();
                semester = Semester.valueOf(setSem.toUpperCase());
                return semester;
            } catch (Exception e) {
                System.out.println("неверные данные, семестр введен неверно");
            }
        }


    }


    public Person setPerson() throws InputException {
        try {
            String name = setName();
            LocalDateTime bDay = setBirthday();
            double weight = setWeight();
            return new Person(name, bDay, weight, setLocation());
        } catch (Exception e) {
            throw new InputException();
        }
    }

    private LocalDateTime setBirthday() throws InputException {
        while (true) {
            try {
                String birthdayStr = userScanner.nextLine().trim();
                if (birthdayStr.isEmpty()) throw new IllegalArgumentException();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("ru", "Ru"));
                LocalDateTime bd = LocalDate.parse(birthdayStr, formatter).atStartOfDay();
                if (bd.isAfter(LocalDate.now().atStartOfDay())) throw new WrongCommandArgsException();

                return bd;
            } catch (WrongCommandArgsException e) {
                System.out.println(e.getMessage());
                throw new InputException();
            } catch (DateTimeParseException e) {
                System.out.println("неверный формат даты!");
                throw new InputException();
            } catch (Exception e) {
                System.out.println("Некорректный ввод. Попробуйте еще раз, например, 1111-11-11");
                throw new InputException();
            }
        }

    }

    private double setWeight() throws InputException {
        while (true) {
            try {
                String line = userScanner.nextLine();
                double weight = Long.parseLong(line);
                if (weight <= 0) {
                    throw new WrongCommandArgsException();
                }
                return weight;
            } catch (Exception e) {
                System.out.println("Некорректный ввод. Попробуйте еще раз, вес не тот");
                throw new InputException();
            }
        }

    }

    public Location setLocation() throws InputException {
        String line;
        Float x;
        Integer y;
        double z;
        while (true) {
            try {
                line = userScanner.nextLine().trim();
                if (line.isEmpty()) throw new InputException();
                x = Float.parseFloat(line);

                line = userScanner.nextLine().trim();
                if (line.isEmpty()) throw new InputException();
                y = Integer.parseInt(line);

                line = userScanner.nextLine().trim();
                z = Double.parseDouble(line);

                return new Location(x, y, z);
            } catch (InputException e) {
                throw new InputException();
            } catch (NumberFormatException e) {
                throw new InputException();
            }
        }
    }
}




