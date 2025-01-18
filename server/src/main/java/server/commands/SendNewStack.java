package server.commands;

import common.actions.Console;
import common.actions.User;
import common.models.StudyGroup;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;

import java.util.ArrayList;
import java.util.Stack;

public class SendNewStack extends Command {
    public static int groupsCount;
    private CollectionManager collectionManager;

    public SendNewStack(CollectionManager collectionManager) {
        super("sendNewStack", "взять коллекцию");
        this.collectionManager = collectionManager;
    }

    public boolean execute(String argument, Object objectArgument, User user) {
        return true;
    }

    public ArrayList<Stack<StudyGroup>> execute2() {
        Stack<StudyGroup> collection = null;
        try {
            collection = collectionManager.getStackCollection();
            Console.println(collection.size()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Stack<StudyGroup>> listOfStacks = new ArrayList<>();
        assert collection != null;
        int siz = collection.size();
        int counter = 0;
        int dataLength = 0;

        Stack<StudyGroup> newStudyGroups = new Stack<>();
        while (counter < siz) {
            newStudyGroups.add(collection.get(counter));

            dataLength += collection.get(counter).toString().length();
            if (dataLength >= 8192) {
                Console.println("i=" + counter + ", dataLength=" + dataLength);
                dataLength = 0;
                listOfStacks.add(newStudyGroups);
                newStudyGroups = new Stack<>();
            }
            counter++;
        }
        if (!newStudyGroups.isEmpty()) listOfStacks.add(newStudyGroups);


        return listOfStacks;

    }
}
