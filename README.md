# ODK Form Builder

A desktop app for building ODK forms visually. Drag form controls onto a
canvas, edit their properties, see validation errors as you type, and export
the result as an XLSForm spreadsheet or an ODK XML form definition.

Written in Java with Swing. No external dependencies.

## Why

ODK forms are normally authored in XLSForm, a spreadsheet format with its own
syntax for question types, constraints, choice lists, groups and repeats. It
works, but it puts a wall in front of anyone who isn't comfortable writing
formulas in a spreadsheet. This builds the same file through a GUI.

## Build and run

Requires a JDK 17 or newer. Nothing else.

```
mkdir build
javac -d build $(find src -name "*.java")
java -cp build odkbuilder.Main
```

On Windows:

```
mkdir build
dir /s /b src\*.java > sources.txt
javac -d build @sources.txt
java -cp build odkbuilder.Main
```

There are also `build.sh` and `build.bat` that do the above in one step.

## Usage

1. Drag a control from the palette onto the canvas.
   - Drop on a **Group** or **Repeat** to nest inside it.
   - Drop on a question to place the new control below it.
2. Click any node to edit it in the Properties panel.
3. For select questions, type a list name, press **Use this list**, then add
   choices. Other questions can reuse the same list from the dropdown.
4. Validation errors appear at the bottom as you work. Click one to jump to
   the question that caused it.
5. Export as `.xlsx` or `.xml`.

## Features

- Nine control types: text, integer, decimal, date, note, select one, select
  multiple, group, repeat
- Nested groups and repeats to any depth
- Full property editing: name, label, hint, required, default, constraint,
  constraint message, relevance, appearance
- Shared choice lists — define `yes_no` once, use it in any number of
  questions
- Live validation: unique names, name syntax, missing labels, empty or
  missing choice lists, malformed constraint expressions
- Export to XLSForm (survey, choices and settings sheets)
- Export to ODK XML

## Not implemented

- **XLSForm import.** Reading an existing spreadsheet back into the editor.
- **Undo.**
- The constraint checker looks for unbalanced brackets, unpaired quotes and a
  few common mistakes. It is not a full XPath parser.
- The XML exporter writes repeats as plain groups and lists choices inline
  rather than as shared itemsets.

## Structure

The app follows MVVM. Nothing below `view` imports Swing.

```
src/odkbuilder/
  Main.java          entry point
  SelfTest.java      builds and exports a form with no GUI

  model/             the form itself — a tree of nodes, plus choice lists
  validation/        one class per authoring rule
  export/            XLSForm and ODK XML writers
  viewmodel/         presentation logic, sits between model and view
  view/              Swing window, palette, canvas, inspector, error list
```

Three patterns carry the design:

- **Composite** — `FormNode` is the base type, questions are leaves, groups
  and repeats are composites. A form is a tree, so the model is one too.
- **Strategy** — validation rules and exporters both sit behind an interface
  with a context object choosing between them. Adding a rule or an export
  format means writing one class.
- **Observer** — model notifies viewmodel, viewmodel notifies view. The model
  has never heard of the window.

Run `java -cp build odkbuilder.SelfTest` to build a form, validate it, and
export both formats from the command line without opening a window.

## Background

Built as a coursework project for CSE 3203 (Object-Oriented Software
Analysis, Design and Development) at the University of Guyana.