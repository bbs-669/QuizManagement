import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

// Serializable Student class for saving/loading
class Student implements Serializable {
    public String name, regNo;
    public int noQuiz;
    public double total, avg;

    Student(String n, String rn, int nq, double t) {
        name = n;
        regNo = rn;
        noQuiz = nq;
        total = t;
        avg = this.average();
    }

    public double average() {
        return (this.noQuiz <= 0) ? 0 : (double) (this.total / this.noQuiz);
    }
}

// Quiz Management handles student data
class QuizManagement implements Serializable {
    public ArrayList<Student> students = new ArrayList<>();

    public void add(Student s) {
        students.add(s);
    }

    public String display() {
        if (students.isEmpty()) return "No students available.";
        StringBuilder details = new StringBuilder();
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            details.append(String.format(
                "[%d] Name: %s\nRegister No: %s\nNo. of Quizzes: %d\nTotal: %.2f\nAverage: %.2f\n\n",
                i + 1, s.name, s.regNo, s.noQuiz, s.total, s.average()
            ));
        }
        return details.toString();
    }
}

// Main Application Frame
class Frame extends JFrame implements ActionListener {
    JTextField name, regNo, tot, noq;
    JButton add, search, display, movef, movel, moven, movep, clear, save;
    JTextArea ta;
    JScrollPane scrollPane;
    QuizManagement qm = new QuizManagement();
    int currentIndex = -1;

    Frame() {
        loadData(); // Load saved data on start

        // Panel 1: Input Fields
        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        panel1.add(new JLabel("Name:"));
        name = new JTextField(15);
        panel1.add(name);

        panel1.add(new JLabel("Register No:"));
        regNo = new JTextField(15);
        panel1.add(regNo);

        panel1.add(new JLabel("Total Marks:"));
        tot = new JTextField(10);
        panel1.add(tot);

        panel1.add(new JLabel("No of Quizzes:"));
        noq = new JTextField(10);
        panel1.add(noq);

        // Panel 2: Buttons
        JPanel panel2 = new JPanel();
        String[] btnNames = {"Add", "Search", "Display", "Move First", "Move Last", "Move Next", "Move Previous", "Clear", "Save"};
        JButton[] btns = new JButton[btnNames.length];
        for (int i = 0; i < btnNames.length; i++) {
            btns[i] = new JButton(btnNames[i]);
            btns[i].addActionListener(this);
            panel2.add(btns[i]);
        }
        add = btns[0]; search = btns[1]; display = btns[2];
        movef = btns[3]; movel = btns[4]; moven = btns[5];
        movep = btns[6]; clear = btns[7]; save = btns[8];

        // TextArea with Scroll
        ta = new JTextArea(20, 100);
        ta.setEditable(false);
        scrollPane = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Layout and Frame Setup
        setLayout(new FlowLayout());
        add(panel1);
        add(panel2);
        add(scrollPane);
        getContentPane().setBackground(new Color(200, 220, 255));
        setTitle("Student Quiz Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 600);
        setResizable(false);
        setVisible(true);
    }

    /** Display details of a specific student */
    private void showStudent(Student s) {
        ta.setText(String.format(
            "Name: %s\nRegister No: %s\nNo. of Quizzes: %d\nTotal: %.2f\nAverage: %.2f\n(Current Student: %d/%d)",
            s.name, s.regNo, s.noQuiz, s.total, s.average(),
            currentIndex + 1, qm.students.size()
        ));
    }

    /** Save student data to file */
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("students.dat"))) {
            oos.writeObject(qm);
            ta.setText("Data saved successfully!");
        } catch (Exception e) {
            ta.setText("Error saving data!");
        }
    }

    /** Load student data from file */
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("students.dat"))) {
            qm = (QuizManagement) ois.readObject();
        } catch (Exception e) {
            qm = new QuizManagement(); // start fresh if no file exists
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == add) {
            try {
                String n = name.getText().trim();
                String reg = regNo.getText().trim();
                if (n.isEmpty() || reg.isEmpty() || tot.getText().trim().isEmpty() || noq.getText().trim().isEmpty()) {
                    ta.setText("Please fill all fields!");
                    return;
                }
                int nq = Integer.parseInt(noq.getText().trim());
                double totalMarks = Double.parseDouble(tot.getText().trim());

                qm.add(new Student(n, reg, nq, totalMarks));
                name.setText(""); regNo.setText(""); noq.setText(""); tot.setText("");
                ta.setText("Student added successfully!\nTotal Students: " + qm.students.size());
            } catch (NumberFormatException ex) {
                ta.setText("Please enter valid numeric values for Total and No. of Quizzes!");
            }
        }

        else if (src == display) {
            ta.setText(qm.display());
        }

        else if (src == search) {
            String regn = regNo.getText().trim();
            boolean found = false;
            for (int i = 0; i < qm.students.size(); i++) {
                Student s = qm.students.get(i);
                if (s.regNo.equalsIgnoreCase(regn)) {
                    currentIndex = i;
                    showStudent(s);
                    found = true;
                    break;
                }
            }
            if (!found) ta.setText("Student not found!");
        }

        else if (src == movef) {
            if (qm.students.isEmpty()) { ta.setText("No students available!"); return; }
            currentIndex = 0;
            showStudent(qm.students.get(currentIndex));
        }

        else if (src == movel) {
            if (qm.students.isEmpty()) { ta.setText("No students available!"); return; }
            currentIndex = qm.students.size() - 1;
            showStudent(qm.students.get(currentIndex));
        }

        else if (src == moven) {
            if (qm.students.isEmpty()) { ta.setText("No students available!"); return; }
            if (currentIndex < qm.students.size() - 1) {
                currentIndex++;
                showStudent(qm.students.get(currentIndex));
            } else ta.setText("Already at last student!");
        }

        else if (src == movep) {
            if (qm.students.isEmpty()) { ta.setText("No students available!"); return; }
            if (currentIndex > 0) {
                currentIndex--;
                showStudent(qm.students.get(currentIndex));
            } else ta.setText("Already at first student!");
        }

        else if (src == clear) {
            ta.setText("");
            name.setText(""); regNo.setText(""); noq.setText(""); tot.setText("");
        }

        else if (src == save) {
            saveData();
        }
    }
}

// Main Class
public class StudentQuiz {
    public static void main(String args[]) {
        new Frame();
    }
}
