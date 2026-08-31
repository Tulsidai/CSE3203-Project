# ODK Form Builder

Build ODK forms without touching a spreadsheet. Drag controls onto a canvas,
edit their properties, watch validation errors appear as you type, and export
to XLSForm (`.xlsx`) or ODK XML.

Java and Swing. No external libraries.

## Why

ODK forms are normally written in XLSForm, which means knowing its syntax for
question types, constraints, choice lists, groups and repeats. That is a wall
for the field staff and researchers who actually need to build the forms. This
puts a GUI in front of it and writes the same file.

## Build and run

Needs JDK 17 or newer, nothing else.

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

## Usage

1. Drag a control from the palette onto the canvas. Drop it on a group or
   repeat to nest it inside; drop it on a question to place it below.
2. Click any node to edit it in the properties panel.
3. For select questions, type a list name, press **Use this list**, then add
   choices. Any other question can pick the same list from the dropdown.
4. Errors appear at the bottom as you work. Click one to jump to the question
   that caused it.
5. Export as `.xlsx` or `.xml`.

## What it does

Nine control types: text, integer, decimal, date, note, select one, select
multiple, group, repeat. Groups and repeats nest to any depth.

Every property is editable: name, label, hint, required, default, constraint,
constraint message, relevance, appearance.

Choice lists are shared. Define `yes_no` once and use it in as many questions
as you like - it still appears on the choices sheet only once.

Validation runs continuously: unique names, name syntax, missing labels, empty
or missing choice lists, malformed constraints.

## What it does not do

- **XLSForm import.** Reading a spreadsheet back into the editor.
- **Undo.**
- The constraint checker catches unbalanced brackets, unpaired quotes and a
  few common mistakes. It is not an XPath parser.
- The XML exporter writes repeats as plain groups and lists choices inline
  instead of as shared itemsets.

## Structure

MVVM. Nothing below `view` imports Swing.

```
src/odkbuilder/
  Main.java          entry point
  SelfTest.java      builds and exports a form with no GUI

  model/             the form itself, a tree of nodes plus choice lists
  validation/        one class per authoring rule
  export/            XLSForm and ODK XML writers
  viewmodel/         presentation logic between model and view
  view/              window, palette, canvas, inspector, error list
```

Three patterns hold it together:

- **Composite** - `FormNode` is the base type, questions are leaves, groups
  and repeats are composites. The form is a tree, so the model is one too.
- **Strategy** - validation rules and exporters each sit behind an interface
  with a context object picking between them. A new rule or format is one
  new class.
- **Observer** - model tells viewmodel, viewmodel tells view. The model has
  never heard of the window.

`java -cp build odkbuilder.SelfTest` builds a form, validates it and exports
both formats from the command line, without opening a window.

## Background

Coursework for CSE 3203 (Object-Oriented Software Analysis, Design and
Development) at the University of Guyana.
