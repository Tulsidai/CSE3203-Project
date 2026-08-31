package odkbuilder.model;

import java.util.ArrayList;

/*
 * The observable side of the Observer pattern.
 *
 * Two things need observing, the Form and the FormViewModel,
 * so the three methods live here once instead of being written twice.
 */
public abstract class Observable {

    private ArrayList<FormObserver> observers = new ArrayList<FormObserver>();

    public void addObserver(FormObserver o) {
        observers.add(o);
    }
    public void removeObserver(FormObserver o) {
        observers.remove(o);
    }

    // Subclass calls this when its state change. everybody gets told, and each one goes and
    // reads back what it needs.
    protected void notifyObservers() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).update();
        }
    }
}
