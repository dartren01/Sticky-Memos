package StickyMemo;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Memo {

	private String SETTINGSNAME = "settings.txt";

	public Memo stickyNote;

	private String postName;
	private String postPath;
	private String postContent;
	private String cName;
	private String postTitle;
	
	private Color postColor;
	private Color barColor;
	
	private Colors color;

	private FileCheckers fileChecker;

	private JMenuBar mb;
	private JMenu menu;
	private JMenuItem newNoteMenu, colorsMenu, delNoteMenu, notesMenu;

	private JTextArea area;
	private JTextField titleText;

	private JFrame f;
	
	private JScrollPane scrollPane;

	private BorderLayout layout;

	private JPanel buttonPanel;
	private JPanel textFieldPanel;
	private JPanel titlePanel;

	public class Colors {
		Color yellow = new Color(255, 250, 205);
		Color YELLOW = new Color(255,246,178);
		
		Color pink = new Color(255, 228, 241);
		Color PINK = new Color(255, 169, 210);
		
		Color blue = new Color(205, 233, 255);
		Color BLUE = new Color(151, 210, 255);
		
		Color green = new Color(203, 241, 196);
		Color GREEN = new Color(153, 241, 139);
		
		Color grey = new Color(224, 224, 224);
		Color GREY = new Color(186, 186, 186);
		
		Color red = new Color(255, 150, 150);
	}

	public Memo(String name, String path, String content, String colorName, String title, int loc) {
		// Create New Frame
		f = new JFrame("Sticky Memos");
		postName = name;
		postPath = path;
		postContent = content;
		cName = colorName;
		postTitle = title;
		fileChecker = new FileCheckers();

		color = new Colors();

		// Set Frame Layout
		layout = new BorderLayout(0, 10);
		f.setLayout(layout);
		
		// for editor IDE
		// File imgfile = new File("src/Image/stickymemo64.png");
		
		// for deployment
		File imgfile = new File("bin/Images/stickymemo64.png");
		
		String imgPath = imgfile.getAbsolutePath();
		ImageIcon img = new ImageIcon(imgPath);
		f.setIconImage(img.getImage());

		// set frame size and position 350 width and 500 height
		f.setSize(350, 500);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
		Point newLocation = new Point(middle.x - (f.getWidth() / 2) + loc, middle.y - (f.getHeight() / 2) + loc);
		f.setLocation(newLocation);

		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Create a new Text Field
		textFieldPanel = new JPanel();
		textFieldPanel.setLayout(new CardLayout(0, 15));
		textFieldPanel.setBorder(BorderFactory.createEmptyBorder());

		// Set initial text field to the text from file
		area = new JTextArea(content);
		area.setBorder(BorderFactory.createEmptyBorder());
		area.setLineWrap(true);
		fileChecker.writeFile(postPath, postName, area.getText());
		area.setFont(new Font("Arial", Font.PLAIN, 20));

		// Add listeners to document updates, write to file
		area.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				postContent = area.getText();
				fileChecker.writeFile(postPath, postName, postContent);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				postContent = area.getText();
				fileChecker.writeFile(postPath, postName, postContent);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				postContent = area.getText();
				fileChecker.writeFile(postPath, postName, postContent);
			}
		});

		// Add a scroller to the text field
		scrollPane = new JScrollPane(area);
		scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		textFieldPanel.add(scrollPane);
		textFieldPanel.setPreferredSize(textFieldPanel.getPreferredSize());

		titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout(10, 0));
		titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 25, 15));

		titleText = new JTextField(title);
		titleText.setBorder(BorderFactory.createEmptyBorder());
		titleText.setFont(new Font("Arial", Font.BOLD, 28));
		titleText.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				postTitle = titleText.getText();
				writeToSetting();
				if (MemoAllNotes.allNotes != null) {
					MemoAllNotes.allNotes.repaint();
				}
				f.setVisible(true);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				postTitle = titleText.getText();
				writeToSetting();
				if (MemoAllNotes.allNotes != null) {
					MemoAllNotes.allNotes.repaint();
				}
				f.setVisible(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				postTitle = titleText.getText();
				writeToSetting();
				if (MemoAllNotes.allNotes != null) {
					MemoAllNotes.allNotes.repaint();
				}
				f.setVisible(true);
			}
		});

		titlePanel.add(titleText, BorderLayout.NORTH);
		titlePanel.add(textFieldPanel, BorderLayout.CENTER);
		titlePanel.setPreferredSize(textFieldPanel.getPreferredSize());

		// Create Button Panel For Colors
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.setBackground(postColor);
		addColorButtons(color, area);
		buttonPanel.setPreferredSize(buttonPanel.getPreferredSize());
		buttonPanel.setVisible(false);

		// Create Menu Bar
		mb = new JMenuBar();
		mb.setBorderPainted(false);
		
		UIManager.put("Menu.font", new Font("Arial", Font.BOLD, 18));
		menu = new JMenu(". . .");
		menu.setPreferredSize(new Dimension(40, 30));
		menu.setBackground(Color.WHITE);
		UIManager.put("MenuItem.font", new Font("Arial", Font.BOLD, 16));
		
		newNoteMenu = new JMenuItem("New Note");
		colorsMenu = new JMenuItem("Colors");
		notesMenu = new JMenuItem("All Notes");
		delNoteMenu = new JMenuItem("Delete Note");
		delNoteMenu.setForeground(Color.RED);
		
		newNoteMenu.setBackground(Color.WHITE);
		colorsMenu.setBackground(Color.WHITE);
		delNoteMenu.setBackground(Color.WHITE);
		notesMenu.setBackground(Color.WHITE);
		
		newNoteMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				StickyMemoMain.createNewPostIt("note", path, StickyMemoMain.DEFAULTTEXTAREA, StickyMemoMain.DEFAULTCOLOR,
						StickyMemoMain.DEFAULTTITLE, false, 0);
			}
		});

		colorsMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (buttonPanel.isVisible()) {
					buttonPanel.setVisible(false);
				} else {
					buttonPanel.setVisible(true);
				}
			}
		});

		notesMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (MemoAllNotes.allNotes == null) {
					MemoAllNotes.allNotes = new MemoAllNotes();
				} else {
					MemoAllNotes.allNotes.bringBack();
				}
			}
		});

		delNoteMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String settingString = fileChecker.readFile(postPath, SETTINGSNAME);
				String[] lineArr = settingString.split("\n");
				String[] newArr = new String[lineArr.length - 1];
				int index = 0;
				for (int i = 0; i < lineArr.length; i++) {
					String[] lineSplit = lineArr[i].split(" ");
					if (lineSplit[0].equals(postName)) {
						index = 1;
						continue;
					}
					newArr[i - index] = lineArr[i];
				}
				String changedText = String.join("\n", newArr);

				fileChecker.writeFile(postPath, SETTINGSNAME, changedText);
				fileChecker.deleteFile(postPath + postName);
				onClose(true);
			}
		});

		menu.add(newNoteMenu);
		menu.add(colorsMenu);
		menu.add(notesMenu);
		menu.add(delNoteMenu);

		mb.add(menu);

		f.setJMenuBar(mb);

		// Add all of the panels to the JFrame

		f.setPreferredSize(f.getPreferredSize());
		f.add(buttonPanel, BorderLayout.NORTH);
		f.add(titlePanel, BorderLayout.CENTER);
		
		// Set Background Colors
		setColors(colorName);

		// making the frame visible
		f.setVisible(true);
	}

	private void addColorButtons(Colors color, JTextArea area) {
		GridBagConstraints c = new GridBagConstraints();

		JButton yellowB = new JButton("");
		yellowB.setBorder(BorderFactory.createEmptyBorder());
		yellowB.setPreferredSize(new Dimension(10, 30));
		yellowB.setBackground(color.YELLOW);
		yellowB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonActionHelper("Yellow");
			}
		});
		
		JButton pinkB = new JButton("");
		pinkB.setBorder(BorderFactory.createEmptyBorder());
		pinkB.setPreferredSize(new Dimension(10, 30));
		pinkB.setBackground(color.PINK);
		pinkB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonActionHelper("Pink");
			}
		});

		JButton blueB = new JButton("");// creating instance of JButton
		blueB.setBorder(BorderFactory.createEmptyBorder());
		blueB.setPreferredSize(new Dimension(10, 30));
		blueB.setBackground(color.BLUE);
		blueB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonActionHelper("Blue");
			}
		});

		JButton greenB = new JButton("");
		greenB.setBorder(BorderFactory.createEmptyBorder());
		greenB.setPreferredSize(new Dimension(10, 30));
		greenB.setBackground(color.GREEN);
		greenB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonActionHelper("Green");
			}
		});

		JButton greyB = new JButton("");
		greyB.setBorder(BorderFactory.createEmptyBorder());
		greyB.setPreferredSize(new Dimension(10, 30));
		greyB.setBackground(color.GREY);
		greyB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonActionHelper("Grey");
			}
		});
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		buttonPanel.add(yellowB, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 1;
		c.gridy = 0;
		buttonPanel.add(pinkB, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 2;
		c.gridy = 0;
		buttonPanel.add(blueB, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 3;
		c.gridy = 0;
		buttonPanel.add(greenB, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 4;
		c.gridy = 0;
		buttonPanel.add(greyB, c);
	}
	
	private void setColors(String colorName) {
		if (colorName.equals("Blue")) {
			postColor = color.blue;
			barColor = color.BLUE;
		} else if (colorName.equals("Green")) {
			postColor = color.green;
			barColor = color.GREEN;
		} else if (colorName.equals("Grey")) {
			postColor = color.grey;
			barColor = color.GREY;
		} else if (colorName.equals("Pink")) {
			postColor = color.pink;
			barColor = color.PINK;
		} else {
			postColor = color.yellow;
			barColor = color.YELLOW;
		}
		setBackgroundColor(postColor);
		mb.setBackground(barColor);
		cName = colorName;
	}
	
	private void ButtonActionHelper(String colorName) {
		setColors(colorName);
		writeToSetting();
		if (MemoAllNotes.allNotes != null) {
			MemoAllNotes.allNotes.repaint();
		}
		buttonPanel.setVisible(false);
	}

	private void writeToSetting() {
		String settingsContent = fileChecker.readFile(postPath, SETTINGSNAME);
		String[] lineArr = settingsContent.split("\n");
		String newContent = "";
		for (int i = 0; i < lineArr.length; i++) {
			String[] lineSplit = lineArr[i].split(" ");
			if (lineSplit[0].equals(postName)) {
				newContent = lineSplit[0] + " " + cName + " " + postTitle;
				lineArr[i] = newContent;
				break;
			}
		}
		String changedText = String.join("\n", lineArr);
		fileChecker.writeFile(postPath, SETTINGSNAME, changedText);
	}

	private void onClose(boolean isDelete) {
		if (isDelete) {
			StickyMemoMain.MemoArr.remove(this);
			if (MemoAllNotes.allNotes != null) {
				MemoAllNotes.allNotes.repaint();
			}
		}
		f.setVisible(false);
		f.dispose();
	}

	private void setBackgroundColor(Color bgColor) {
		f.getContentPane().setBackground(bgColor);
		textFieldPanel.setBackground(bgColor);
		area.setBackground(bgColor);
		titlePanel.setBackground(bgColor);
		titleText.setBackground(bgColor);
		scrollPane.getVerticalScrollBar().setBackground(bgColor);
		scrollPane.getHorizontalScrollBar().setBackground(bgColor);
	}

	public String getname() {
		return postName;
	}

	public String getpath() {
		return postPath;
	}

	public String getcontent() {
		return postContent;
	}

	public Color getcolor() {
		return postColor;
	}

	public String gettitle() {
		return postTitle;
	}

	public void bringBack() {
		f.setVisible(true);
		f.toFront();
	}

	public void addAllNotes() {
		for (int i = 0; i < StickyMemoMain.MemoArr.size(); i++) {
			notesMenu.add(new JMenuItem(StickyMemoMain.MemoArr.get(i).postName));
		}
	}

}
