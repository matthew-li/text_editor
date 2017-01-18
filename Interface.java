import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

/** The interface of the TextEditor.
 * 	@author Matthew E. Li
 */
public class Interface extends JPanel implements ActionListener, DocumentListener, KeyListener, WindowListener {

	/* The serial version UID. */
	private static final long serialVersionUID = -3478250244690912983L;
	
    /* The set of fonts. */
	private static final String[] FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    /* The set of text sizes. */
	private static final String[] TEXT_SIZES = new String[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36", "48", "72", "144"};
    /* The set of text styles. */
    private static final String[] TEXT_STYLES = new String[]{"Regular", "Bold", "Italic", "Bold Italic"};
    
    /* The starting indices of the last word searched for. */
    private List<Integer> locations;
    /* Whether the last direction chosen during a find operation was up (0) or down (1). */
    private boolean lastDirection = true;
    /* Whether or not the find operation should match case. */
    private boolean matchCase = false;
    /* The document associated with the frame's text area. */
    private Document document;
    /* The index of the last found word. */
    private int lastPointer = 0;
    /* The pointer to the current state in the list of states. */
    private int statePointer = 0;
    /* The size of text displayed. */
    private int textSize = 20;
    /* The style of the text. */
    private int textStyle = Font.PLAIN;
    /* The file chooser. */
    private JFileChooser fc = new JFileChooser();
    /* The interface frame. */
    private JFrame frame;
    /* The status. */
    private JLabel status;
    /* The menus included in the menu bar. */
    private JMenu editMenu, fileMenu, formatMenu, viewMenu;
    /* The menu bar. */
    private JMenuBar menuBar;
    /* The buttons included in the editMenu. */
    private JMenuItem copyButton, cutButton, deleteButton, findButton, findNextButton, goToButton, pasteButton, redoButton, replaceButton, selectAllButton, timeDateButton, undoButton;
    /* The buttons included in the fileMenu. */
    private JMenuItem exitButton, newButton, openButton, saveButton, saveAsButton;
    /* The buttons included in the formatMenu. */
    private JMenuItem fontButton, textSizeButton, textStyleButton, wordWrapButton;
    /* The buttons included in the viewMenu. */
    private JMenuItem statusBarButton;
    /* The status bar. */
    private JPanel statusBar;
    /* The scroll pane of the frame. */
    private JScrollPane scrollPane;
    /* The text area of the frame. */
    private JTextArea textArea;
    /* The list of states of text. */
    private List<State> states;
    /* The path of the current file being written to. */
    private String filePath;
    /* The current font. */
    private String font = "Times New Roman";
    /* The last word replacing another word. */
    private String lastReplacing = "";
    /* The last word searched for. */
    private String lastSearched = "";
    /* The writer that writes to the output file. */
    private Writer writer;
	
	/** Instantiate a new Interface. */
	public Interface() {
		/* Create the interface frame. */
		frame = new JFrame(displayFileName() + " - TextEditor");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this);
		frame.add(this);
		
		/* Create the status bar. */
		statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		status = new JLabel("Ln 1, Col 1");
		statusBar.add(status);
		/* Initially set the status bar to invisible. */
		statusBar.setVisible(false);
		/* Add the status bar to the frame. */
		frame.add(statusBar, BorderLayout.SOUTH);

		/* Create the menu bar. */
		menuBar = new JMenuBar();
		
		/* Create the edit menu. */
		editMenu = new JMenu("Edit");
		/* Create buttons for the edit menu. */
		copyButton = new JMenuItem(new DefaultEditorKit.CopyAction());
		copyButton.setText("Copy");
		cutButton = new JMenuItem(new DefaultEditorKit.CutAction());
		cutButton.setText("Cut");
		deleteButton = new JMenuItem("Delete");
		findButton = new JMenuItem("Find...");
		findNextButton = new JMenuItem("Find Next");
		goToButton = new JMenuItem("Go To...");
		pasteButton = new JMenuItem(new DefaultEditorKit.PasteAction());
		pasteButton.setText("Paste");
		redoButton = new JMenuItem("Redo");
		replaceButton = new JMenuItem("Replace...");
		selectAllButton = new JMenuItem("Select All");
		timeDateButton = new JMenuItem("Time/Date");
		undoButton = new JMenuItem("Undo");
		/* Add keystroke labels to edit menu items. */
		copyButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		cutButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		deleteButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		findButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		findNextButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		goToButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		pasteButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		redoButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		replaceButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		selectAllButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		timeDateButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		undoButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		/* Add listeners to the edit menu buttons. */
		deleteButton.addActionListener(buttonListener());
		findButton.addActionListener(buttonListener());
		findNextButton.addActionListener(buttonListener());
		goToButton.addActionListener(buttonListener());
		redoButton.addActionListener(buttonListener());
		replaceButton.addActionListener(buttonListener());
		selectAllButton.addActionListener(buttonListener());
		timeDateButton.addActionListener(buttonListener());
		undoButton.addActionListener(buttonListener());
		/* Set initial enabled statuses for edit buttons. */
		copyButton.setEnabled(false);
		cutButton.setEnabled(false);
		deleteButton.setEnabled(false);
		findButton.setEnabled(false);
		findNextButton.setEnabled(false);
		redoButton.setEnabled(false);
		replaceButton.setEnabled(false);
		undoButton.setEnabled(false);
		/* Add the buttons to the edit menu in the desired order. */
		editMenu.add(undoButton);
		editMenu.add(redoButton);
		editMenu.addSeparator();
		editMenu.add(cutButton);
		editMenu.add(copyButton);
		editMenu.add(pasteButton);
		editMenu.add(deleteButton);
		editMenu.addSeparator();
		editMenu.add(findButton);
		editMenu.add(findNextButton);
		editMenu.add(replaceButton);
		editMenu.add(goToButton);
		editMenu.addSeparator();
		editMenu.add(selectAllButton);
		editMenu.add(timeDateButton);
		
		/* Create the file menu. */
		fileMenu = new JMenu("File");
		/* Create buttons for the file menu. */
		exitButton = new JMenuItem("Exit");
		newButton = new JMenuItem("New");
		openButton = new JMenuItem("Open...");
		saveButton = new JMenuItem("Save");
		saveAsButton = new JMenuItem("Save As...");
		/* Add keystroke labels to file menu items. */
		newButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		openButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		saveButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		/* Add listeners to the file menu buttons. */
		exitButton.addActionListener(buttonListener());
		newButton.addActionListener(buttonListener());
		openButton.addActionListener(buttonListener());
		saveButton.addActionListener(buttonListener());
		saveAsButton.addActionListener(buttonListener());
		/* Add the buttons to the file menu in the desired order. */
		fileMenu.add(newButton);
		fileMenu.add(openButton);
		fileMenu.add(saveButton);
		fileMenu.add(saveAsButton);
		fileMenu.addSeparator();
		fileMenu.add(exitButton);
		
		/* Create the format menu. */
		formatMenu = new JMenu("Format");
		/* Create buttons for the format menu. */
		fontButton = new JMenuItem("Font");
		textSizeButton = new JMenuItem("Text Size");
		textStyleButton = new JMenuItem("Text Style");
		wordWrapButton = new JCheckBoxMenuItem("Word Wrap");
		/* Add listeners to the format menu buttons. */
		fontButton.addActionListener(buttonListener());
		textSizeButton.addActionListener(buttonListener());
		textStyleButton.addActionListener(buttonListener());
		wordWrapButton.addActionListener(buttonListener());
		/* Add the buttons to the format menu in the desired order. */
		formatMenu.add(wordWrapButton);
		formatMenu.add(fontButton);
		formatMenu.add(textSizeButton);
		formatMenu.add(textStyleButton);

		/* Create the view menu. */
		viewMenu = new JMenu("View");
		/* Create buttons for the view menu. */
		statusBarButton = new JCheckBoxMenuItem("Status Bar");
		/* Add listeners to the view menu buttons. */
		statusBarButton.addActionListener(buttonListener());
		/* Add the buttons to the view menu in the desired order. */
		viewMenu.add(statusBarButton);
		
		/* Add the menus to the menu bar in the desired order. */
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(formatMenu);
		menuBar.add(viewMenu);
		
		/* Set the frame's menu bar. */
		frame.setJMenuBar(menuBar);
		
		/* Set the text area of the frame. */
		textArea = new JTextArea(30, 80);
		textArea.setFont(new Font(font, textStyle, textSize));
		textArea.addCaretListener(caretListener());
		textArea.addKeyListener(this);
		document = textArea.getDocument();
		document.addDocumentListener(this);
		scrollPane = new JScrollPane(textArea);
		frame.add(scrollPane);
		
		/* Display the frame. */
		frame.pack();
		frame.setVisible(true);
		
		/* Set the file chooser's current directory to the user's directory. */
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				
		/* Create the list of states and add to it the initial state. */
		resetStates();
		
	}
	
	/** Listen for buttons. */
	private ActionListener buttonListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JMenuItem source = (JMenuItem) ae.getSource();
				if (source.equals(deleteButton)) {
					delete();
				} else if (source.equals(findButton)) {
					find();
				} else if (source.equals(findNextButton)) {
					findNextOrPrevious();
				} else if (source.equals(goToButton)) {
					goTo();
				} else if (source.equals(redoButton)) {
					redo();
				} else if (source.equals(replaceButton)) {
					replace();
				} else if (source.equals(selectAllButton)) {
					selectAll();
				} else if (source.equals(timeDateButton)) {
					timeDate();
				} else if (source.equals(undoButton)) {
					undo();
				} else if (source.equals(exitButton)) {
					exitFile();
				} else if (source.equals(newButton)) {
					newFile();
				} else if (source.equals(openButton)) {
					openFile();
				} else if (source.equals(saveButton)) {
					saveFile();
				} else if (source.equals(saveAsButton)) {
					saveAsFile();
				} else if (source.equals(fontButton)) {
					changeFont(textArea);
				} else if (source.equals(textSizeButton)) {
					changeTextSize(textArea);
				} else if (source.equals(textStyleButton)) {
					changeTextStyle(textArea);
				} else if (source.equals(wordWrapButton)) {
					toggleWordWrap(textArea);
				} else if (source.equals(statusBarButton)) {
					toggleStatusBar();
				}
			}
		};
	}
	
	/** Listen for the caret. */
	private CaretListener caretListener() {
		return new CaretListener() {
			public void caretUpdate(CaretEvent ce) {
				int caretPosition = textArea.getCaretPosition();
				try {
					/* Row, column calculations from http://stackoverflow.com/a/5140180 */
					int row = textArea.getLineOfOffset(caretPosition);
					int column = caretPosition - textArea.getLineStartOffset(row) + 1;
					row += 1;
					status.setText("Ln " + row + " Col " + column);
					setStatus(row, column);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	/** Perform a delete operation. */
	private void delete() {
		textArea.setText(textArea.getText().replace(textArea.getSelectedText(), ""));
	}
	
	/** Perform a find operation. */	
	private void find() {
		boolean resolved = false;
//		lastPointer = 0;
		while (!resolved) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			
			JPanel inputPanel = new JPanel();
			JTextField textField = new JTextField(lastSearched, 20);
			inputPanel.add(new JLabel("Find what:"));
			inputPanel.add(textField);
			panel.add(inputPanel);
			
			JPanel optionsPanel = new JPanel();
			JCheckBox matchCaseCheckBox = new JCheckBox();
			matchCaseCheckBox.setSelected(matchCase);
			optionsPanel.add(new JLabel("Match case"));
			optionsPanel.add(matchCaseCheckBox);
			JLabel divider = new JLabel("         ");
			optionsPanel.add(divider);
			ButtonGroup group = new ButtonGroup();
			JLabel directionLabel = new JLabel("Direction");
			optionsPanel.add(directionLabel);
			JRadioButton up = new JRadioButton("Up");
			JRadioButton down = new JRadioButton("Down");
			group.add(up);
			group.add(down);
			optionsPanel.add(up);
			optionsPanel.add(down);
			down.setSelected(lastDirection);
			if (!down.isSelected()) {
				up.setSelected(true);
			}
			panel.add(optionsPanel);

			String[] buttons = {"Find Next", "Cancel"};
			int choice = JOptionPane.showOptionDialog(null,  panel, "Find", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
			lastSearched = textField.getText();
			
			if (down.isSelected() && !lastDirection) {
				lastPointer += 2;
			} else if (up.isSelected() && lastDirection) {
				lastPointer -= 2;
			}
			
			lastDirection = down.isSelected();
			matchCase = matchCaseCheckBox.isSelected();
			if (choice == JOptionPane.OK_OPTION) {
				if (!up.isSelected()) {
					findNext(lastSearched, matchCase);	
				} else {
					findPrevious(lastSearched, matchCase);
				}
			} else {
				resolved = true;
			}
		}
	}

	/** Return all starting indices of instances of WORD in TEXT, regardless of case. */
	private ArrayList<Integer> findAll(String word, String text) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		word = word.toLowerCase();
		text = text.toLowerCase();
		if (word != null && word.length() > 0) {
			int index = text.indexOf(word);
			while (index != -1) {
				indices.add(index);
				index = text.indexOf(word, index + 1);
			}
		}
		return indices;
	}

	/** Call either findNext of findPrevious depending on conditions. */
	private void findNextOrPrevious() {
		if (lastDirection) {
			findNext(lastSearched, matchCase);
		} else {
			findPrevious(lastSearched, matchCase);
		}
	}
	
	/** Perform a find next operation on WORD, given MATCHCASE. */
	private void findNext(String word, boolean matchCase) {
		if (word != null && word.length() > 0) {
			locations = findAll(word, textArea.getText());
			if (locations.size() == 0) {
				JOptionPane.showMessageDialog(null, "Cannot find \"" + word + "\"");
			} else {
				boolean resolved = false;
				while (!resolved) {
					try {
						textArea.setCaretPosition(locations.get(lastPointer));
						int position = textArea.getCaretPosition();
						textArea.select(position, position + word.length());
						if (!matchCase || textArea.getSelectedText().equals(word)) {
							resolved = true;
						}
						lastPointer += 1;
						if (lastPointer <= 0 || lastPointer >= locations.size()) {
							lastPointer = 0;
						}
					} catch (IndexOutOfBoundsException e) {
						lastPointer -= 1;
					}
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "Please enter a valid search term.");
		}
	}
	
	/** Perform a find previous operation on WORD, given MATCHCASE. */
	private void findPrevious(String word, boolean matchCase) {
		if (word != null && word.length() > 0) {
			locations = findAll(word, textArea.getText());
			if (locations.size() == 0) {
				JOptionPane.showMessageDialog(null, "Cannot find \"" + word + "\"");
			} else {
				boolean resolved = false;
				while (!resolved) {
					try {
						textArea.setCaretPosition(locations.get(lastPointer));
						int position = textArea.getCaretPosition();
						textArea.select(position, position + word.length());
						if (!matchCase || textArea.getSelectedText().equals(word)) {
							resolved = true;
						}
						lastPointer -= 1;
						if (lastPointer < 0 || lastPointer >= locations.size()) {
							lastPointer = locations.size() - 1;
						}
					} catch (IndexOutOfBoundsException e) {
						lastPointer += 1;
					}
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "Please enter a valid search term.");
		}
	}
	
	/** Perform a go to operation. */
	private void goTo() {
		boolean resolved = false;
		int lineNumber = 0;
		while (!resolved) {
			String response = JOptionPane.showInputDialog(null, "Line number:", "Go To Line", JOptionPane.INFORMATION_MESSAGE);
			if (response != null && response.length() > 0) {
				try {
					lineNumber = Integer.parseInt(response) - 1;
					if (lineNumber < 0 || lineNumber >= textArea.getLineCount()) {
						JOptionPane.showMessageDialog(null, "The line number is beyond the total number of lines.");
					} else {
						textArea.setCaretPosition(textArea.getDocument().getDefaultRootElement().getElement(lineNumber).getStartOffset());
						resolved = true;
					}
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Please enter a number.");
				}	
			} else {
				resolved = true;
			}
		}
	}
	
	/** Perform a redo operation. */
	private void redo() {
		document.removeDocumentListener(this);
		try {
			State newState = states.get(statePointer + 1);
			textArea.setText(newState.getText());
			if (statePointer < states.size()) {
				statePointer += 1;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		document.addDocumentListener(this);
		toggleButtons();
	}
	
	/** Perform a replace operation. */
	private void replace() {
		boolean resolved = false;
		while (!resolved) {
			JPanel panel = new JPanel();
		
			JLabel findLabel = new JLabel("Find what:");
			JLabel replaceLabel = new JLabel("Replace with:");
			JLabel matchCaseLabel = new JLabel("Match case");
			
			JTextField textFieldFind = new JTextField(lastSearched, 20);
			JTextField textFieldReplace = new JTextField(lastReplacing, 20);
			
			JCheckBox matchCaseCheckBox = new JCheckBox();
			matchCaseCheckBox.setSelected(matchCase);
			
			GroupLayout layout = new GroupLayout(panel);
			panel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			
			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
			hGroup.addGroup(layout.createParallelGroup().addComponent(findLabel).addComponent(replaceLabel).addComponent(matchCaseLabel));
			hGroup.addGroup(layout.createParallelGroup().addComponent(textFieldFind).addComponent(textFieldReplace).addComponent(matchCaseCheckBox));
			layout.setHorizontalGroup(hGroup);
			
			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(findLabel).addComponent(textFieldFind));
			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(replaceLabel).addComponent(textFieldReplace));
			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(matchCaseLabel).addComponent(matchCaseCheckBox));
			layout.setVerticalGroup(vGroup);
			
			String[] buttons = {"Find Next", "Replace", "Replace All", "Cancel"};
			int choice = JOptionPane.showOptionDialog(null,  panel, "Replace", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
			lastSearched = textFieldFind.getText();
			lastReplacing = textFieldReplace.getText();
			matchCase = matchCaseCheckBox.isSelected();
			switch (choice) {
			case 0:
				findNext(lastSearched, matchCase);
				break;
			case 1:
				locations = findAll(lastSearched, textArea.getText());
				if (lastSearched != null && lastSearched.length() > 0 && lastReplacing != null && lastReplacing.length() > 0) {
					String selected = textArea.getSelectedText();
					if (selected != null && selected.length() > 0) {
						if (!matchCase) {
							textArea.replaceSelection(lastReplacing);
							lastPointer -= 1;
							if (lastPointer <= 0) {
								lastPointer = 0;
							}
						} else {
							if (selected.equals(lastSearched)) {
								textArea.replaceSelection(lastReplacing);
							}
							lastPointer -= 1;
							if (lastPointer <= 0) {
								lastPointer = 0;
							}
						}
					} else {
						if (textArea.getText().indexOf(lastSearched) == -1) {
							JOptionPane.showMessageDialog(null, "Cannot find \"" + lastSearched + "\"");
						} else {
							findNext(lastSearched, matchCase);	
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please fill in both input fields.");
				}
				break;
			case 2:
				locations = findAll(lastSearched, textArea.getText());
				if (locations.size() > 0) {
					if (lastReplacing != null && lastReplacing.length() > 0) {
						if (!matchCase) {
							/* Regex pattern from http://stackoverflow.com/a/5055036 */
							textArea.setText(textArea.getText().replaceAll("(?i)" + Pattern.quote(lastSearched), lastReplacing));	
						} else {
							textArea.setText(textArea.getText().replaceAll(lastSearched, lastReplacing));
						}
					} else {
						JOptionPane.showMessageDialog(null, "Please fill in both input fields.");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Cannot find \"" + lastSearched + "\"");
				}
				break;
			case 3:
				resolved = true;
				break;
			default:
				resolved = true;
				break;
			}
		}
	}
	
	/** Reset states. */
	private void resetStates() {
		states = new ArrayList<State>();
		states.add(new State(textArea.getText(), System.currentTimeMillis() - 1000));
		statePointer = 0;
	}
	
	/** Perform a select all operation. */
	private void selectAll() {
		textArea.selectAll();
	}
	
	/** Perform a time/date operation. */
	private void timeDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a MM/dd/yyyy");
		textArea.insert(sdf.format(new Date()), textArea.getCaretPosition());
	}

	/** Perform an undo operation. */
	private void undo() {
		document.removeDocumentListener(this);
		try {
			State oldState = states.get(statePointer - 1);
			textArea.setText(oldState.getText());
			if (statePointer > 0) {
				statePointer -= 1;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		document.addDocumentListener(this);
		toggleButtons();
	}
	
	/** Exit the program. */
	private void exitFile() {
		if (promptSave()) {
			return;
		}
		filePath = null;
		System.exit(0);
	}
	
	/** Open a new file. */
	private void newFile() {
		if (promptSave()) {
			return;
		}
		filePath = null;
		frame.setTitle(displayFileName() + " - TextEditor");
		textArea.setText("");
		resetStates();
		toggleButtons();
	}
	
	/** Open a file from the file system. */
	private void openFile() {
		if (promptSave()) {
	    	return;
		}
		int choice = fc.showOpenDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION) {
			try {
				filePath = fc.getSelectedFile().getAbsolutePath();
				String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
				textArea.setText(fileContent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			filePath = null;
		}
		frame.setTitle(displayFileName() + " - TextEditor");
		resetStates();
		toggleButtons();
	}
	
	/** Save the current file, returning whether or not the operation was cancelled. */
	private boolean saveFile() {
		if (filePath == null) {
			return saveAsFile();
		} else {
			writer = new Writer(filePath);
			String text = textArea.getText();
			writer.write(text);
			writer.close();
			return false;
		}
	}
	
	/** Save as a new file, returning whether or not the operation was cancelled. */
	private boolean saveAsFile() {
		int choice = fc.showSaveDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION) {
			filePath = fc.getSelectedFile().getAbsolutePath();
			writer = new Writer(filePath);
			String text = textArea.getText();
			writer.write(text);
			writer.close();
			frame.setTitle(displayFileName() + " - TextEditor");
			return false;
		} else {
			return true;
		}
	}
	
	/** Prompt a save operation, returning whether or not the operation was cancelled. */
	private boolean promptSave() {
		if (!textArea.getText().equals("") || filePath != null) {
	    	int save = JOptionPane.showConfirmDialog(null, "Do you want to save changes to " + displayFilePath() + "?", "Save", JOptionPane.YES_NO_CANCEL_OPTION);
	    	if (save == JOptionPane.CANCEL_OPTION) {
	    		return true;
	    	} else if (save == JOptionPane.YES_OPTION) {
	    		return saveFile();
	    	}
		}
		return false;
	}
	
	/** Return a new text area, with the font changed from TEXTAREA. */
	private JTextArea changeFont(JTextArea textArea) {
		font = (String) JOptionPane.showInputDialog(null, "Choose a font.", "Font", JOptionPane.QUESTION_MESSAGE, null, FONTS, font);
		textArea.setFont(new Font(font, textStyle, textSize));
		return textArea;
	}
	
	/** Return a new text area, with the adjusted text size from TEXTAREA. */
	private JTextArea changeTextSize(JTextArea textArea) {
		String textSizeString = (String) JOptionPane.showInputDialog(null, "Choose a text size.", "Text Size", JOptionPane.QUESTION_MESSAGE, null, TEXT_SIZES, Integer.toString(textSize));
		if (textSizeString != null) {
			textSize = Integer.parseInt(textSizeString);
		}
		textArea.setFont(new Font(font, textStyle, textSize));
		return textArea;
	}
	
	/** Return a new text area, with the adjusted text style from TEXTAREA. */
	private JTextArea changeTextStyle(JTextArea textArea) {
		String textStyleString = (String) JOptionPane.showInputDialog(null, "Choose a text style.", "Text Style", JOptionPane.QUESTION_MESSAGE, null, TEXT_STYLES, TEXT_STYLES[textStyle]);
		if (textStyleString != null) {
			switch (textStyleString) {
			case "Regular":
				textStyle = Font.PLAIN;
				break;
			case "Italic":
				textStyle = Font.ITALIC;
				break;
			case "Bold":
				textStyle = Font.BOLD;
				break;
			case "Bold Italic":
				textStyle = Font.ITALIC + Font.BOLD;
				break;
			default:
				break;
			}
		}
		textArea.setFont(new Font(font, textStyle, textSize));
		return textArea;
	}
	
	/** Return a new text area, with word wrap toggled from TEXTAREA. */
	private JTextArea toggleWordWrap(JTextArea textArea) {
		if (textArea.getLineWrap()) {
			textArea.setLineWrap(false);
			textArea.setWrapStyleWord(false);
			goToButton.setEnabled(true);
    		statusBarButton.setEnabled(true);
		} else {
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			goToButton.setEnabled(false);
    		statusBarButton.setEnabled(false);
    		statusBarButton.setSelected(false);
    		statusBar.setVisible(false);
		}
		return textArea;
	}
	
	/** Toggle enabled statuses for various buttons based on conditions. */
	private void toggleButtons() {
		boolean isText = textArea.getText().length() > 0;
		copyButton.setEnabled(isText);
		cutButton.setEnabled(isText);
		deleteButton.setEnabled(isText);
		findButton.setEnabled(isText);
		findNextButton.setEnabled(isText);
		replaceButton.setEnabled(isText);
		undoButton.setEnabled(isText);
		if (!isText) {
    		lastSearched = "";
    		lastPointer = 0;
		}
		undoButton.setEnabled(statePointer > 0);
		redoButton.setEnabled(statePointer < states.size() - 1);
	}
	
	/** Perform the necessary changes to maintain the undo/redo functionality. */
	private void updateUndoRedo() {
		State s1 = states.get(states.size() - 1);
		State s2 = new State(textArea.getText());
		if (states.size() >= 10) {
			states.remove(0);
			statePointer -= 1;
		}
		if (statePointer == states.size() - 1) {
			if (State.differenceSeconds(s1, s2) >= 0.5) {
				states.add(s2);
				statePointer += 1;
			} else {
				s1.setText(textArea.getText());
				states.set(states.size() - 1, s1);
			}
		} else {
			if (State.differenceSeconds(s1, s2) >= 0.5) {
				statePointer += 1;
				states.set(statePointer, s2);
				states = states.subList(0, statePointer + 1);
			} else {
				s1.setText(textArea.getText());
				states.set(states.size() - 1, s1);
				states = states.subList(0, statePointer);
			}
		}
		System.gc();
	}
	
	/** Toggle the status bar's visibility. */
	private void toggleStatusBar() {
		if (statusBar.isVisible()) {
			statusBar.setVisible(false);
		} else {
			statusBar.setVisible(true);
		}
	}
	
	/** Return the file path, or "Untitled" if the path is null. */
	private String displayFilePath() {
		if (filePath == null) {
			return "Untitled";
		}
		return filePath;
	}
	
	/** Return the file name, or "Untitled" if the path is null. */
	private String displayFileName() {
		if (filePath == null) {
			return "Untitled";
		}
		return new File(filePath).getName();
	}
	
	/** Set the status to the row and column position of the cursor. */
	private void setStatus(int row, int column) {
		status.setText("Ln " + row + " Col " + column);
	}
	
	/* Required methods for ActionListener. */
	public void actionPerformed(ActionEvent ae) {}

	/* Required methods for DocumentListener. */
	public void changedUpdate(DocumentEvent de) {
		System.out.println("dfjfslkfjskdjflksdjf");
	}
	public void insertUpdate(DocumentEvent de) {
		updateUndoRedo();
		toggleButtons();
	}
	public void removeUpdate(DocumentEvent de) {
		updateUndoRedo();
		toggleButtons();
	}
	
	/* Required methods for KeyListener. */
	public void keyPressed(KeyEvent ke) {}
    public void keyReleased(KeyEvent ke) {}
    public void keyTyped(KeyEvent ke) {}
    
	/* Required methods for WindowListener. */
    public void windowActivated(WindowEvent we) {}
    public void windowClosing(WindowEvent we) {
    	exitFile();
    }
    public void windowClosed(WindowEvent we) {}
    public void windowDeactivated(WindowEvent we) {}
    public void windowDeiconified(WindowEvent we) {}
    public void windowIconified(WindowEvent we) {}
    public void windowOpened(WindowEvent we) {}
	
}
