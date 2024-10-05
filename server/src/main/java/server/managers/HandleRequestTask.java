package server.managers;

import common.actions.Request;
import common.actions.Response;
import common.actions.ResponseCode;
import common.actions.User;
import server.Server;


import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;

public class HandleRequestTask implements Callable<Response> {
    private static final Logger logger = Logger.getLogger(HandleRequestTask.class.getName());
    private Request request;
    private CommandManager commandManager;
    private Object newObject;

    public HandleRequestTask(Request request, CommandManager commandManager) {
        this.request = request;
        this.commandManager = commandManager;
    }

    @Override
    public Response call() {
        ArrayList<String> array = commandManager.getNewCommands();
        User user = new User(
                request.getUser().getUsername(),
                request.getUser().getPassword()
        );
        ResponseCode responseCode = executeCommand(request.getName(), request.getStringArgument(),
                request.getOtherArguments(), user);

        if (array.contains(request.getName())){
            logger.info("Новый Response сформирован");
            System.out.println(newObject.toString());
            return new Response(newObject, ResponseCode.PEAK_SIZE);

        }else{
            logger.info("Старый Response сформирован");

            return new Response(responseCode, ResponseManager.getAndClear(), ResponseManager.getArgsAndClear());
        }
    }


    private synchronized ResponseCode executeCommand(String command, String commandStringArgument, Object commandObjectArgument, User user) {
        switch (command) {
            case "":
                break;
            case "add":
                if (!commandManager.add(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "help":
                if (!commandManager.help(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "info":
                if (!commandManager.info(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "execute_script":
                if (!commandManager.executeScript(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "update_by_id":
                if (!commandManager.update(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "show":
                if (!commandManager.show(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "clear":
                if (!commandManager.clear(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "shuffle":
                if (!commandManager.shuffle(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "reorder":
                if (!commandManager.reorder(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "print_ascending":
                if (!commandManager.printAscending(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "print_field_ascending_students_count":
                if (!commandManager.printFieldAscendingStudentsCount(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "remove_first":
                if (!commandManager.removeFirst(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "remove_by_id":
                if (!commandManager.removeById(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;

            case "exit":
                if (!commandManager.exit(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                return ResponseCode.CLIENT_EXIT;

            case "login":
                if (!commandManager.login(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "register":
                if (!commandManager.register(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "sendNewStack":
                if (!commandManager.sendNewStack(commandStringArgument, commandObjectArgument, user))
//
                    return ResponseCode.ERROR;
                else{
                    newObject = commandManager.getSendNewStack().execute2();
                }
                break;
            default:
                ResponseManager.appendln("Команда '" + command + "' не найдена. Наберите 'help' для справки.");
                return ResponseCode.ERROR;
        }
        return ResponseCode.OK;
    }
}
