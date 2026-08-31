package odkbuilder;

import java.io.File;
import java.util.ArrayList;
import odkbuilder.export.ODKXMLExporter;
import odkbuilder.export.XLSFormExporter;
import odkbuilder.model.ChoiceItem;
import odkbuilder.model.ChoiceList;
import odkbuilder.model.DateQuestionNode;
import odkbuilder.model.Form;
import odkbuilder.model.ContainerNode;
import odkbuilder.model.IntegerQuestionNode;
import odkbuilder.model.NoteQuestionNode;
import odkbuilder.model.RepeatNode;
import odkbuilder.model.SelectOneQuestionNode;
import odkbuilder.model.TextQuestionNode;
import odkbuilder.validation.FormValidator;
import odkbuilder.validation.ValidationError;

public class SelfTest {
    public static void main(String[] args) throws Exception {
        Form form = new Form("Library Stocktake", "library_stocktake");

        ChoiceList genre = new ChoiceList("genre");
        genre.addChoice(new ChoiceItem("fiction", "Fiction"));
        genre.addChoice(new ChoiceItem("nonfiction", "Non-fiction"));
        genre.addChoice(new ChoiceItem("reference", "Reference"));
        form.addChoiceList(genre);

        ChoiceList condition = new ChoiceList("condition");
        condition.addChoice(new ChoiceItem("good", "Good"));
        condition.addChoice(new ChoiceItem("worn", "Worn"));
        condition.addChoice(new ChoiceItem("damaged", "Damaged"));
        form.addChoiceList(condition);

        form.getRoot().add(new NoteQuestionNode("intro",
                "Complete one form for each title on the shelf."));

        TextQuestionNode title = new TextQuestionNode("book_title", "Title of the book");
        title.setRequired(true);
        form.getRoot().add(title);

        TextQuestionNode author = new TextQuestionNode("author", "Author");
        author.setRequired(true);
        form.getRoot().add(author);

        SelectOneQuestionNode bookGenre =
                new SelectOneQuestionNode("genre", "Genre", "genre");
        form.getRoot().add(bookGenre);

        ContainerNode shelf = new ContainerNode("shelf_location", "Shelf location");
        shelf.add(new TextQuestionNode("section", "Section"));
        shelf.add(new IntegerQuestionNode("shelf_number", "Shelf number"));
        form.getRoot().add(shelf);

        RepeatNode copies = new RepeatNode("copies", "Copies of this title");
        TextQuestionNode accession =
                new TextQuestionNode("accession_no", "Accession number");
        accession.setRequired(true);
        copies.add(accession);

        SelectOneQuestionNode copyCondition =
                new SelectOneQuestionNode("copy_condition", "Condition of this copy", "condition");
        copies.add(copyCondition);

        IntegerQuestionNode timesBorrowed =
                new IntegerQuestionNode("times_borrowed", "Times borrowed this year");
        timesBorrowed.setConstraint(". >= 0");
        timesBorrowed.setConstraintMessage("This cannot be a negative number.");
        copies.add(timesBorrowed);

        TextQuestionNode damageNote =
                new TextQuestionNode("damage_note", "Describe the damage");
        damageNote.setRelevant("${copy_condition} = 'damaged'");
        copies.add(damageNote);

        form.getRoot().add(copies);
        form.getRoot().add(new DateQuestionNode("checked_on", "Date checked"));

        FormValidator validator = new FormValidator();
        ArrayList<ValidationError> errors = validator.validate(form);

        System.out.println("Nodes in the form: " + form.collectAllNodes().size());
        System.out.println("Choice lists: " + form.getChoiceLists().size());
        System.out.println("Errors on the good form: " + errors.size());
        for (int i = 0; i < errors.size(); i++) {
            System.out.println("   " + errors.get(i));
        }

        System.out.println();
        System.out.println("Breaking the form on purpose:");

        TextQuestionNode duplicate = new TextQuestionNode("author", "");
        duplicate.setConstraint("(. > 5");
        form.getRoot().add(duplicate);

        SelectOneQuestionNode broken =
                new SelectOneQuestionNode("2 bad name", "Bad one", "no_such_list");
        form.getRoot().add(broken);
        errors = validator.validate(form);
        for (int i = 0; i < errors.size(); i++) {
            System.out.println("   " + errors.get(i));
        }

        form.getRoot().remove(duplicate);
        form.getRoot().remove(broken);

        System.out.println();
        File spreadsheet = new File("library_stocktake.xlsx");
        new XLSFormExporter().export(form, spreadsheet);
        System.out.println("Wrote " + spreadsheet.getName()
                + "  (" + spreadsheet.length() + " bytes)");

        File xml = new File("library_stocktake.xml");
        new ODKXMLExporter().export(form, xml);
        System.out.println("Wrote " + xml.getName()
                + "  (" + xml.length() + " bytes)");

        System.out.println();
        System.out.println("Self test finished. No window was ever opened.");
    }
}
