package odkbuilder.model;

/*
 * Observer side of the Observer pattern.
 *
 * An interface, not a class, so one object can watch two observables.
 * The ViewModel watches the Form and the View watches the ViewModel.
 */
public interface FormObserver {
    void update();
}
