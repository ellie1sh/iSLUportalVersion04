import java.util.LinkedList;

// Custom class to hold menu data
public class MenuItem {
    private String name;
    private MySinglyLinkedList<String> subItems;

    public MenuItem(String name) {
        this.name = name;
        this.subItems = null; // No sublist by default
    }

    public MenuItem(String name, MySinglyLinkedList<String> subItems) {
        this.name = name;
        this.subItems = subItems;
    }



    public String getName() {
        return name;
    }

    public MySinglyLinkedList<String> getSubItems() {
        return subItems;
    }

    public boolean hasSubItems() {
        return subItems != null && subItems.getSize() > 0 ;
    }
}