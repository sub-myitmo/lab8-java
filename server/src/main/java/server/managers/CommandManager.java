package server.managers;

import common.actions.User;
import server.commands.Command;
import server.commands.SendNewStack;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Класс для запуска команд и хранения истории
 *
 * @author petrovviachesalv
 */
public class CommandManager {
    private Map<String, Command> commands = new HashMap<>();
    private final ArrayList<String> newCommands = new ArrayList<>();

    //private ReadWriteLock historyLocker = new ReentrantReadWriteLock();
    private ReadWriteLock locker = new ReentrantReadWriteLock();

    private Command exitCommand, helpCommand, infoCommand, showCommand, clearCommand, removeByIdCommand, removeFirstCommand, shuffleCommand, reorderCommand, printAscendingCommand, printFieldAscendingStudentsCountCommand, executeScriptCommand, addCommand, updateIdCommand;

    private Command loginCommand, registerCommand;
    private SendNewStack sendNewStack;

    public CommandManager(Command exitCommand, SendNewStack sendNewStackCommand/*Command helpCommand, Command showCommand,*/, Command infoCommand  , Command clearCommand, Command removeByIdCommand, Command removeFirstCommand, Command shuffleCommand, Command reorderCommand, Command printAscendingCommand, Command printFieldAscendingStudentsCountCommand, Command executeScriptCommand, Command addCommand, Command updateIdCommand, Command loginCommand, Command registerCommand) {
        this.exitCommand = exitCommand;
        this.sendNewStack = sendNewStackCommand;
//        this.helpCommand = helpCommand;
        this.infoCommand = infoCommand;
//        this.showCommand = showCommand;
        this.clearCommand = clearCommand;
        this.removeByIdCommand = removeByIdCommand;
        this.removeFirstCommand = removeFirstCommand;
        this.shuffleCommand = shuffleCommand;
        this.reorderCommand = reorderCommand;
        this.printAscendingCommand = printAscendingCommand;
        this.printFieldAscendingStudentsCountCommand = printFieldAscendingStudentsCountCommand;
        this.executeScriptCommand = executeScriptCommand;
        this.addCommand = addCommand;
        this.updateIdCommand = updateIdCommand;
        this.loginCommand = loginCommand;
        this.registerCommand = registerCommand;

        newCommands.add(sendNewStackCommand.getName());

        commands.put(exitCommand.getName(), exitCommand);
        //commands.put(helpCommand.getName(), helpCommand);
        commands.put(infoCommand.getName(), infoCommand);
        //commands.put(showCommand.getName(), showCommand);
        commands.put(clearCommand.getName(), clearCommand);
        commands.put(removeByIdCommand.getName(), removeByIdCommand);
        commands.put(removeFirstCommand.getName(), removeFirstCommand);
        commands.put(shuffleCommand.getName(), shuffleCommand);
        commands.put(reorderCommand.getName(), reorderCommand);
        commands.put(printAscendingCommand.getName(), printAscendingCommand);
        commands.put(printFieldAscendingStudentsCountCommand.getName(), printFieldAscendingStudentsCountCommand);
        commands.put(executeScriptCommand.getName(), executeScriptCommand);
        commands.put(addCommand.getName(), addCommand);
        commands.put(updateIdCommand.getName(), updateIdCommand);

        commands.put(loginCommand.getName(), loginCommand);
        commands.put(registerCommand.getName(), registerCommand);

    }


    public boolean add(String stringArgument, Object objectArgument, User user) {
        try {
            locker.writeLock().lock();
            return addCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.writeLock().unlock();
        }
    }

    public boolean help(String stringArgument, Object objectArgument, User user) {
        if (helpCommand.execute(stringArgument, objectArgument, user)) {
            commands.values().stream().filter(command -> command.getName()!="register" && command.getName()!="login").forEach(command -> ResponseManager.appendargs(command.getName(), command.getDescription()));
            return true;
        } else return false;
    }

    public boolean show(String stringArgument, Object objectArgument, User user) {
        try {
            locker.readLock().lock();
            return showCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.readLock().unlock();
        }
    }

    public boolean info(String stringArgument, Object objectArgument, User user) {
        try {
            locker.readLock().lock();
            return infoCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.readLock().unlock();
        }
    }

    public boolean clear(String stringArgument, Object objectArgument, User user) {
        try {
            locker.writeLock().lock();
            return clearCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.writeLock().unlock();
        }
    }

    public boolean exit(String stringArgument, Object objectArgument, User user) {
        return exitCommand.execute(stringArgument, objectArgument, user);
    }

    public boolean executeScript(String stringArgument, Object objectArgument, User user) {
        return executeScriptCommand.execute(stringArgument, objectArgument, user);
    }

    public boolean removeFirst(String stringArgument, Object objectArgument, User user) {
        try {
            locker.writeLock().lock();
            return removeFirstCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.writeLock().unlock();
        }
    }

    public boolean removeById(String stringArgument, Object objectArgument, User user) {

        try {
            locker.writeLock().lock();
            return removeByIdCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.writeLock().unlock();
        }
    }

    public boolean login(String stringArgument, Object objectArgument, User user) {
        return loginCommand.execute(stringArgument, objectArgument, user);
    }

    public boolean register(String stringArgument, Object objectArgument, User user) {
        return registerCommand.execute(stringArgument, objectArgument, user);
    }

    public boolean shuffle(String stringArgument, Object objectArgument, User user) {
        try {
            locker.writeLock().lock();
            return shuffleCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.writeLock().unlock();
        }
    }

    public boolean reorder(String stringArgument, Object objectArgument, User user) {
        try {
            locker.writeLock().lock();
            return reorderCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.writeLock().unlock();
        }
    }

    public boolean update(String stringArgument, Object objectArgument, User user) {
        try {
            locker.writeLock().lock();
            return updateIdCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.writeLock().unlock();
        }
    }

    public boolean printAscending(String stringArgument, Object objectArgument, User user) {
        try {
            locker.readLock().lock();
            return printAscendingCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.readLock().unlock();
        }
    }
    public boolean printFieldAscendingStudentsCount(String stringArgument, Object objectArgument, User user) {
        try {
            locker.readLock().lock();
            return printFieldAscendingStudentsCountCommand.execute(stringArgument, objectArgument, user);
        } finally {
            locker.readLock().unlock();
        }
    }

    public ArrayList<String> getNewCommands(){

        return newCommands;

    }

    public boolean sendNewStack(String stringArgument, Object objectArgument, User user){
        locker.readLock().lock();
        try {
            return sendNewStack.execute(stringArgument, objectArgument, user);
        }finally {
            locker.readLock().unlock();
        }
    }

    public SendNewStack getSendNewStack(){
        locker.readLock().lock();
        try {
            return sendNewStack;
        } finally {
            locker.readLock().unlock();
        }
    }



}