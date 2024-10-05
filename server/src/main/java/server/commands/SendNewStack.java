package server.commands;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Stack<StudyGroup>> listOfStacks = new ArrayList<>();
        assert collection != null;
        int siz = collection.size();
        groupsCount = siz / 50;
        int count = 0;
        if (groupsCount > 0) {
            for (int i = 0; i < groupsCount; i++) {
                Stack<StudyGroup> newStudyGroups = new Stack<>();
                for (int j = 50 * count; j < 50 + 50 * count; j++) {
                    newStudyGroups.add(collection.get(j));
                }
                listOfStacks.add(newStudyGroups);
                count++;
            }
        }
        Stack<StudyGroup> ostatStudyGroups = new Stack<>();
        for (int k = groupsCount * 50; k < collection.size(); k++) {
            ostatStudyGroups.add(collection.get(k));

        }
        listOfStacks.add(ostatStudyGroups);

        return listOfStacks;

    }
}
