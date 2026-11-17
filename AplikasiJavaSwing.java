import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;

/**
 * Proyek pengembangan materi Week 6 - Java Swing.
 * Fitur: Form Registrasi, Editor Teks, Data Mahasiswa, Login,
 * Daftar Belanja, Layout Manager, Menu, Dialog, dan Jam Digital.
 */
public class AplikasiJavaSwing extends JFrame {
    private final JTabbedPane tabs = new JTabbedPane();

    // Form registrasi
    private JTextField regNama, regEmail;
    private JPasswordField regSandi;
    private JRadioButton regLaki, regPerempuan;
    private JComboBox<String> regProdi;
    private JCheckBox hobiMembaca, hobiMusik, hobiOlahraga, hobiCoding;

    // Editor teks
    private JTextArea editor;
    private File fileAktif;

    // Data mahasiswa
    private DefaultTableModel modelMahasiswa;
    private JTable tabelMahasiswa;
    private JTextField nimField, namaField, prodiField, ipkField;

    // Daftar belanja
    private DefaultListModel<String> modelBelanja;
    private JList<String> listBelanja;
    private JTextField itemBelanja;

    // Jam digital
    private JLabel jamLabel;
    private Timer timer;

    public AplikasiJavaSwing() {
        super("Java Swing Desktop Application");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);

        buatMenu();
        buatToolbar();

        tabs.addTab("Registrasi", buatPanelRegistrasi());
        tabs.addTab("Editor Teks", buatPanelEditor());
        tabs.addTab("Data Mahasiswa", buatPanelMahasiswa());
        tabs.addTab("Login & Belanja", buatPanelLoginBelanja());
        tabs.addTab("Layout & Jam", buatPanelLayoutJam());

        add(tabs, BorderLayout.CENTER);
        add(buatStatusBar(), BorderLayout.SOUTH);
        aturLookAndFeel();
        pack();
        setLocationRelativeTo(null);
    }

    private void aturLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            System.out.println("Tema Nimbus tidak tersedia, menggunakan tema bawaan.");
        }
    }

    private JPanel buatStatusBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(new EmptyBorder(3, 8, 3, 8));
        p.add(new JLabel("Pemrograman 1 | Week 6 - Java Swing"));
        return p;
    }

    private void buatMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem baru = new JMenuItem("Baru");
        baru.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        baru.addActionListener(e -> editor.setText(""));
        JMenuItem buka = new JMenuItem("Buka");
        buka.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        buka.addActionListener(e -> bukaFile());
        JMenuItem simpan = new JMenuItem("Simpan");
        simpan.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        simpan.addActionListener(e -> simpanFile());
        JMenuItem keluar = new JMenuItem("Keluar");
        keluar.addActionListener(e -> konfirmasiKeluar());
        file.add(baru); file.add(buka); file.add(simpan);
        file.addSeparator(); file.add(keluar);

        JMenu edit = new JMenu("Edit");
        JMenuItem potong = new JMenuItem(new DefaultEditorKit.CutAction());
        potong.setText("Potong");
        JMenuItem salin = new JMenuItem(new DefaultEditorKit.CopyAction());
        salin.setText("Salin");
        JMenuItem tempel = new JMenuItem(new DefaultEditorKit.PasteAction());
        tempel.setText("Tempel");
        edit.add(potong); edit.add(salin); edit.add(tempel);

        JMenu bantuan = new JMenu("Bantuan");
        JMenuItem tentang = new JMenuItem("Tentang");
        tentang.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Java Swing Desktop Application\nDibuat untuk tugas Pemrograman 1 - Week 6.",
                "Tentang Aplikasi", JOptionPane.INFORMATION_MESSAGE));
        bantuan.add(tentang);

        bar.add(file); bar.add(edit); bar.add(bantuan);
        setJMenuBar(bar);
    }

    private void buatToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton baru = new JButton("Baru");
        baru.addActionListener(e -> { tabs.setSelectedIndex(1); editor.setText(""); });
        JButton buka = new JButton("Buka");
        buka.addActionListener(e -> { tabs.setSelectedIndex(1); bukaFile(); });
        JButton simpan = new JButton("Simpan");
        simpan.addActionListener(e -> { tabs.setSelectedIndex(1); simpanFile(); });
        JButton warna = new JButton("Warna Editor");
        warna.addActionListener(e -> pilihWarnaEditor());
        toolbar.add(baru); toolbar.add(buka); toolbar.add(simpan);
        toolbar.addSeparator(); toolbar.add(warna);
        add(toolbar, BorderLayout.NORTH);
    }

    // TUGAS 1 - Form Registrasi menggunakan GridBagLayout
    private JPanel buatPanelRegistrasi() {
        JPanel luar = new JPanel(new BorderLayout(10, 10));
        luar.setBorder(new EmptyBorder(18, 22, 18, 22));
        JLabel judul = new JLabel("FORM REGISTRASI MAHASISWA");
        judul.setFont(new Font("SansSerif", Font.BOLD, 20));
        luar.add(judul, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 7, 7, 7);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        regNama = new JTextField(22);
        regEmail = new JTextField(22);
        regSandi = new JPasswordField(22);
        regLaki = new JRadioButton("Laki-laki", true);
        regPerempuan = new JRadioButton("Perempuan");
        ButtonGroup gender = new ButtonGroup();
        gender.add(regLaki); gender.add(regPerempuan);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        genderPanel.add(regLaki); genderPanel.add(regPerempuan);

        regProdi = new JComboBox<>(new String[]{"Informatika", "Sistem Informasi", "Teknologi Informasi", "Manajemen"});
        hobiMembaca = new JCheckBox("Membaca");
        hobiMusik = new JCheckBox("Musik");
        hobiOlahraga = new JCheckBox("Olahraga");
        hobiCoding = new JCheckBox("Coding");
        JPanel hobiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        hobiPanel.add(hobiMembaca); hobiPanel.add(hobiMusik);
        hobiPanel.add(hobiOlahraga); hobiPanel.add(hobiCoding);

        tambahBarisForm(form, c, 0, "Nama Lengkap:", regNama);
        tambahBarisForm(form, c, 1, "Email:", regEmail);
        tambahBarisForm(form, c, 2, "Password:", regSandi);
        tambahBarisForm(form, c, 3, "Jenis Kelamin:", genderPanel);
        tambahBarisForm(form, c, 4, "Program Studi:", regProdi);
        tambahBarisForm(form, c, 5, "Hobi:", hobiPanel);

        JButton daftar = new JButton("Daftar");
        daftar.setMnemonic('D');
        daftar.addActionListener(e -> prosesRegistrasi());
        JButton reset = new JButton("Reset");
        reset.addActionListener(e -> resetRegistrasi());
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tombol.add(daftar); tombol.add(reset);

        JPanel tengah = new JPanel(new BorderLayout());
        tengah.add(form, BorderLayout.NORTH);
        tengah.add(tombol, BorderLayout.CENTER);
        luar.add(tengah, BorderLayout.CENTER);
        return luar;
    }

    private void tambahBarisForm(JPanel panel, GridBagConstraints c, int baris, String label, JComponent input) {
        c.gridy = baris; c.gridx = 0; c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(input, c);
    }

    private void prosesRegistrasi() {
        String nama = regNama.getText().trim();
        String email = regEmail.getText().trim();
        char[] sandi = regSandi.getPassword();
        if (nama.isEmpty() || email.isEmpty() || sandi.length == 0) {
            JOptionPane.showMessageDialog(this, "Nama, email, dan password wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Format email belum valid.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder hobi = new StringBuilder();
        if (hobiMembaca.isSelected()) hobi.append("Membaca, ");
        if (hobiMusik.isSelected()) hobi.append("Musik, ");
        if (hobiOlahraga.isSelected()) hobi.append("Olahraga, ");
        if (hobiCoding.isSelected()) hobi.append("Coding, ");
        String pilihanHobi = hobi.length() == 0 ? "Tidak ada" : hobi.substring(0, hobi.length() - 2);
        String ringkasan = "Nama: " + nama + "\nEmail: " + email
                + "\nGender: " + (regLaki.isSelected() ? "Laki-laki" : "Perempuan")
                + "\nProdi: " + regProdi.getSelectedItem() + "\nHobi: " + pilihanHobi;
        JOptionPane.showMessageDialog(this, ringkasan, "Ringkasan Registrasi",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetRegistrasi() {
        regNama.setText(""); regEmail.setText(""); regSandi.setText("");
        regLaki.setSelected(true); regProdi.setSelectedIndex(0);
        hobiMembaca.setSelected(false); hobiMusik.setSelected(false);
        hobiOlahraga.setSelected(false); hobiCoding.setSelected(false);
    }

    // TUGAS 1 - Editor Teks dengan JTextArea, JScrollPane, JFileChooser, dan popup menu
    private JPanel buatPanelEditor() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        editor = new JTextArea(20, 70);
        editor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editor.setLineWrap(true);
        editor.setWrapStyleWord(true);
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem(new DefaultEditorKit.CutAction())).setText("Potong");
        popup.add(new JMenuItem(new DefaultEditorKit.CopyAction())).setText("Salin");
        popup.add(new JMenuItem(new DefaultEditorKit.PasteAction())).setText("Tempel");
        editor.setComponentPopupMenu(popup);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton buka = new JButton("Buka File"); buka.addActionListener(e -> bukaFile());
        JButton simpan = new JButton("Simpan File"); simpan.addActionListener(e -> simpanFile());
        JButton warna = new JButton("Pilih Warna"); warna.addActionListener(e -> pilihWarnaEditor());
        tombol.add(buka); tombol.add(simpan); tombol.add(warna);
        p.add(tombol, BorderLayout.NORTH);
        p.add(new JScrollPane(editor), BorderLayout.CENTER);
        return p;
    }

    private void bukaFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileAktif = fc.getSelectedFile();
            try {
                editor.setText(Files.readString(fileAktif.toPath(), StandardCharsets.UTF_8));
                editor.setCaretPosition(0);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal membuka file: " + ex.getMessage(),
                        "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void simpanFile() {
        JFileChooser fc = new JFileChooser();
        if (fileAktif != null) fc.setSelectedFile(fileAktif);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileAktif = fc.getSelectedFile();
            try {
                Files.writeString(fileAktif.toPath(), editor.getText(), StandardCharsets.UTF_8);
                JOptionPane.showMessageDialog(this, "File berhasil disimpan.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + ex.getMessage(),
                        "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pilihWarnaEditor() {
        Color warna = JColorChooser.showDialog(this, "Pilih warna teks", editor.getForeground());
        if (warna != null) editor.setForeground(warna);
    }

    // TUGAS 1 - Data Mahasiswa: JTable, DefaultTableModel, tambah, ubah, hapus
    private JPanel buatPanelMahasiswa() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        modelMahasiswa = new DefaultTableModel(new Object[]{"NIM", "Nama", "Program Studi", "IPK"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelMahasiswa = new JTable(modelMahasiswa);
        tabelMahasiswa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelMahasiswa.setRowHeight(24);
        tabelMahasiswa.setFillsViewportHeight(true);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelMahasiswa);
        tabelMahasiswa.setRowSorter(sorter);

        JTextField pencarian = new JTextField(22);
        pencarian.setToolTipText("Cari berdasarkan NIM, nama, atau program studi");
        pencarian.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String kata = pencarian.getText().trim();
                sorter.setRowFilter(kata.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(kata)));
            }
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
        JPanel pencarianPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pencarianPanel.add(new JLabel("Cari mahasiswa:"));
        pencarianPanel.add(pencarian);
        p.add(pencarianPanel, BorderLayout.NORTH);
        p.add(new JScrollPane(tabelMahasiswa), BorderLayout.CENTER);

        JPanel input = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 5, 4, 5); c.fill = GridBagConstraints.HORIZONTAL;
        nimField = new JTextField(10); namaField = new JTextField(14);
        prodiField = new JTextField(14); ipkField = new JTextField(5);
        tambahInputMahasiswa(input, c, 0, "NIM", nimField);
        tambahInputMahasiswa(input, c, 1, "Nama", namaField);
        tambahInputMahasiswa(input, c, 2, "Prodi", prodiField);
        tambahInputMahasiswa(input, c, 3, "IPK", ipkField);

        JButton tambah = new JButton("Tambah");
        tambah.addActionListener(e -> tambahMahasiswa());
        JButton ubah = new JButton("Ubah Data Terpilih");
        ubah.addActionListener(e -> ubahMahasiswa());
        JButton hapus = new JButton("Hapus");
        hapus.addActionListener(e -> hapusMahasiswa());
        JButton bersih = new JButton("Bersihkan");
        bersih.addActionListener(e -> bersihkanInputMahasiswa());
        JButton ekspor = new JButton("Ekspor CSV");
        ekspor.addActionListener(e -> eksporMahasiswaCSV());
        JPanel aksi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aksi.add(tambah); aksi.add(ubah); aksi.add(hapus); aksi.add(bersih); aksi.add(ekspor);

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.add(input, BorderLayout.CENTER); bawah.add(aksi, BorderLayout.SOUTH);
        p.add(bawah, BorderLayout.SOUTH);

        tabelMahasiswa.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelMahasiswa.getSelectedRow() >= 0) {
                int r = tabelMahasiswa.convertRowIndexToModel(tabelMahasiswa.getSelectedRow());
                nimField.setText(modelMahasiswa.getValueAt(r, 0).toString());
                namaField.setText(modelMahasiswa.getValueAt(r, 1).toString());
                prodiField.setText(modelMahasiswa.getValueAt(r, 2).toString());
                ipkField.setText(modelMahasiswa.getValueAt(r, 3).toString());
            }
        });
        return p;
    }

    private void tambahInputMahasiswa(JPanel p, GridBagConstraints c, int x, String label, JTextField field) {
        c.gridx = x * 2; c.gridy = 0; c.weightx = 0;
        p.add(new JLabel(label + ":"), c);
        c.gridx = x * 2 + 1; c.weightx = 1;
        p.add(field, c);
    }

    private boolean validasiMahasiswa() {
        if (nimField.getText().trim().isEmpty() || namaField.getText().trim().isEmpty()
                || prodiField.getText().trim().isEmpty() || ipkField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua data mahasiswa wajib diisi.");
            return false;
        }
        try {
            double ipk = Double.parseDouble(ipkField.getText().trim());
            if (ipk < 0 || ipk > 4) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "IPK harus berupa angka antara 0 sampai 4.");
            return false;
        }
        return true;
    }

    private Object[] dataMahasiswa() {
        return new Object[]{nimField.getText().trim(), namaField.getText().trim(),
                prodiField.getText().trim(), ipkField.getText().trim()};
    }

    private void tambahMahasiswa() {
        if (!validasiMahasiswa()) return;
        for (int i = 0; i < modelMahasiswa.getRowCount(); i++) {
            if (modelMahasiswa.getValueAt(i, 0).toString().equals(nimField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "NIM sudah terdaftar."); return;
            }
        }
        modelMahasiswa.addRow(dataMahasiswa());
        bersihkanInputMahasiswa();
    }

    private void ubahMahasiswa() {
        int viewRow = tabelMahasiswa.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(this, "Pilih baris yang akan diubah."); return; }
        int r = tabelMahasiswa.convertRowIndexToModel(viewRow);
        if (!validasiMahasiswa()) return;
        String nimBaru = nimField.getText().trim();
        for (int i = 0; i < modelMahasiswa.getRowCount(); i++) {
            if (i != r && modelMahasiswa.getValueAt(i, 0).toString().equals(nimBaru)) {
                JOptionPane.showMessageDialog(this, "NIM sudah digunakan mahasiswa lain.");
                return;
            }
        }
        modelMahasiswa.setValueAt(nimBaru, r, 0);
        modelMahasiswa.setValueAt(namaField.getText().trim(), r, 1);
        modelMahasiswa.setValueAt(prodiField.getText().trim(), r, 2);
        modelMahasiswa.setValueAt(ipkField.getText().trim(), r, 3);
        bersihkanInputMahasiswa();
    }

    private void hapusMahasiswa() {
        int viewRow = tabelMahasiswa.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus."); return; }
        int r = tabelMahasiswa.convertRowIndexToModel(viewRow);
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Hapus data terpilih?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) modelMahasiswa.removeRow(r);
        bersihkanInputMahasiswa();
    }

    private void eksporMahasiswaCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("data_mahasiswa.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        StringBuilder csv = new StringBuilder("NIM,Nama,Program Studi,IPK\n");
        for (int i = 0; i < modelMahasiswa.getRowCount(); i++) {
            for (int j = 0; j < modelMahasiswa.getColumnCount(); j++) {
                String nilai = modelMahasiswa.getValueAt(i, j).toString().replace("\"", "\"\"");
                csv.append('"').append(nilai).append('"');
                if (j < modelMahasiswa.getColumnCount() - 1) csv.append(',');
            }
            csv.append('\n');
        }
        try {
            Files.writeString(chooser.getSelectedFile().toPath(), csv.toString(), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this, "Data mahasiswa berhasil diekspor ke CSV.", "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengekspor data: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bersihkanInputMahasiswa() {
        nimField.setText(""); namaField.setText(""); prodiField.setText(""); ipkField.setText("");
        tabelMahasiswa.clearSelection();
    }

    // TUGAS 2 - Form Login dan daftar belanja dengan JList
    private JPanel buatPanelLoginBelanja() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 12));
        p.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel login = new JPanel(new GridBagLayout());
        login.setBorder(BorderFactory.createTitledBorder("Form Login"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6); c.fill = GridBagConstraints.HORIZONTAL;
        JTextField username = new JTextField(14);
        JPasswordField password = new JPasswordField(14);
        JButton masuk = new JButton("Masuk");
        tambahBarisLogin(login, c, 0, "Username:", username);
        tambahBarisLogin(login, c, 1, "Password:", password);
        c.gridx = 1; c.gridy = 2;
        login.add(masuk, c);
        masuk.addActionListener(e -> {
            if (username.getText().trim().isEmpty() || password.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Username dan password tidak boleh kosong.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Input berhasil divalidasi.\nDemo ini belum menggunakan database akun.");
            }
        });

        JPanel belanja = new JPanel(new BorderLayout(6, 6));
        belanja.setBorder(BorderFactory.createTitledBorder("Daftar Belanja"));
        itemBelanja = new JTextField();
        JButton tambah = new JButton("Tambah");
        JPanel atas = new JPanel(new BorderLayout(5, 5));
        atas.add(itemBelanja, BorderLayout.CENTER); atas.add(tambah, BorderLayout.EAST);
        modelBelanja = new DefaultListModel<>();
        listBelanja = new JList<>(modelBelanja);
        listBelanja.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JButton hapus = new JButton("Hapus Terpilih");
        tambah.addActionListener(e -> tambahItemBelanja());
        itemBelanja.addActionListener(e -> tambahItemBelanja());
        hapus.addActionListener(e -> {
            int index = listBelanja.getSelectedIndex();
            if (index >= 0) modelBelanja.remove(index);
            else JOptionPane.showMessageDialog(this, "Pilih item yang akan dihapus.");
        });
        belanja.add(atas, BorderLayout.NORTH);
        belanja.add(new JScrollPane(listBelanja), BorderLayout.CENTER);
        belanja.add(hapus, BorderLayout.SOUTH);

        p.add(login); p.add(belanja);
        return p;
    }

    private void tambahBarisLogin(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; p.add(new JLabel(label), c);
        c.gridx = 1; p.add(field, c);
    }

    private void tambahItemBelanja() {
        String item = itemBelanja.getText().trim();
        if (item.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama barang belum diisi.");
            return;
        }
        modelBelanja.addElement(item);
        itemBelanja.setText("");
        itemBelanja.requestFocus();
    }

    // BONUS - FlowLayout, GridLayout, javax.swing.Timer
    private JPanel buatPanelLayoutJam() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel demo = new JPanel(new BorderLayout(8, 8));
        demo.setBorder(BorderFactory.createTitledBorder("Demo Layout Manager"));
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        for (int i = 1; i <= 3; i++) tombol.add(new JButton("Tombol " + i));
        demo.add(tombol, BorderLayout.CENTER);

        JButton flow = new JButton("FlowLayout");
        flow.addActionListener(e -> {
            tombol.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            tombol.revalidate(); tombol.repaint();
        });
        JButton grid = new JButton("GridLayout 1 x 3");
        grid.addActionListener(e -> {
            tombol.setLayout(new GridLayout(1, 3, 8, 8));
            tombol.revalidate(); tombol.repaint();
        });
        JPanel pilihan = new JPanel(); pilihan.add(flow); pilihan.add(grid);
        demo.add(pilihan, BorderLayout.SOUTH);

        JPanel jamPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        jamPanel.setBorder(BorderFactory.createTitledBorder("Jam Digital"));
        jamLabel = new JLabel("--:--:--", SwingConstants.CENTER);
        jamLabel.setFont(new Font("SansSerif", Font.BOLD, 38));
        JButton mulai = new JButton("Mulai Jam");
        JButton berhenti = new JButton("Hentikan Jam");
        JPanel kontrol = new JPanel(); kontrol.add(mulai); kontrol.add(berhenti);
        jamPanel.add(jamLabel); jamPanel.add(kontrol);

        timer = new Timer(1000, e -> jamLabel.setText(
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        mulai.addActionListener(e -> timer.start());
        berhenti.addActionListener(e -> timer.stop());
        jamLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        p.add(demo, BorderLayout.CENTER);
        p.add(jamPanel, BorderLayout.SOUTH);
        return p;
    }

    private void konfirmasiKeluar() {
        int pilih = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar dari aplikasi?",
                "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
        if (pilih == JOptionPane.YES_OPTION) {
            if (timer != null) timer.stop();
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AplikasiJavaSwing().setVisible(true));
    }
}
