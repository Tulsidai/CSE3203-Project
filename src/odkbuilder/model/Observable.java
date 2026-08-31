package odkbuilder.model;

import java.util.ArrayList;

public abstract class Observable {
    private ArrayList<FormObserver> observers = new ArrayList<FormObserver>();

    public void addObserver(FormObserver o) {
        observers.add(o);
    }
    public void removeObserver(FormObserver o) {
        observers.remove(o);
    }

    protected void notifyObservers() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).update();
        }
    }
}
