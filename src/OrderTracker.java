import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

@SuppressWarnings("ALL")
public class OrderTracker extends JFrame implements ActionListener, DropTargetListener {
    String imgadd = null;
    JFrame window;

    private static final String APP_ICON_PATH = "src/res/logo.png";


    public static void main(String[] args) {
        SwingUtilities.invokeLater(OrderTracker::new);
    }

    public OrderTracker() {
        createDropWindow();
        createDropTarget();
    }

    private void createDropWindow() {
        window = new JFrame("Order Tracker");
        window.setMinimumSize(new Dimension(985, 610));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon appIcon = new ImageIcon(APP_ICON_PATH);
        window.setIconImage(appIcon.getImage());


        DefaultTableModel tableModel = new DefaultTableModel(getColumnHeaders(), 0);
        JTable table = new JTable(tableModel);

        Image backgroundImage = null;
        try {
            imgadd = ("res/backg.png");
            backgroundImage = ImageIO.read(Objects.requireNonNull(OrderTracker.class.getResource(imgadd)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert backgroundImage != null;

        int originalWidth = backgroundImage.getWidth(null);
        int originalHeight = backgroundImage.getHeight(null);
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        double widthRatio = (double) windowWidth / originalWidth;
        double heightRatio = (double) windowHeight / originalHeight;

        double scale = Math.min(widthRatio, heightRatio);

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)));

        int xPosition = (windowWidth - scaledWidth) / 2;
        int yPosition = (windowHeight - scaledHeight) / 2;

        backgroundLabel.setBounds(xPosition, yPosition, scaledWidth, scaledHeight);

        JPanel contentPanel = new JPanel(null);
        contentPanel.setBackground(Color.white);
        contentPanel.add(backgroundLabel);

        window.setContentPane(contentPanel);
        window.setVisible(true);
    }

    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract(value = " -> new", pure = true)
    private String[] getColumnHeaders() {
        return new String[]{"Seller Gstin", "Invoice Number", "Invoice Date", "Transaction Type", "Order Id", "Shipment Id", "Shipment Date", "Order Date", "Shipment Item Id", "Quantity", "Item Description", "Asin", "Hsn/sac", "Sku", "Product Tax Code", "Bill From City", "Bill From State", "Bill From Country", "Bill From Postal Code", "Ship From City", "Ship From State", "Ship From Country", "Ship From Postal Code", "Ship To City", "Ship To State", "Ship To Country", "Ship To Postal Code", "Invoice Amount", "Tax Exclusive Gross", "Total Tax Amount", "Cgst Rate", "Sgst Rate", "Utgst Rate", "Igst Rate", "Compensatory Cess Rate", "Principal Amount", "Principal Amount Basis", "Cgst Tax", "Sgst Tax", "Utgst Tax", "Igst Tax", "Compensatory Cess Tax", "Shipping Amount", "Shipping Amount Basis", "Shipping Cgst Tax", "Shipping Sgst Tax", "Shipping Utgst Tax", "Shipping Igst Tax", "Shipping Cess Tax", "Gift Wrap Amount", "Gift Wrap Amount Basis", "Gift Wrap Cgst Tax", "Gift Wrap Sgst Tax", "Gift Wrap Utgst Tax", "Gift Wrap Igst Tax", "Gift Wrap Compensatory Cess Tax", "Item Promo Discount", "Item Promo Discount Basis", "Item Promo Tax", "Shipping Promo Discount", "Shipping Promo Discount Basis", "Shipping Promo Tax", "Gift Wrap Promo Discount", "Gift Wrap Promo Discount Basis", "Gift Wrap Promo Tax", "Tcs Cgst Rate", "Tcs Cgst Amount", "Tcs Sgst Rate", "Tcs Sgst Amount", "Tcs Utgst Rate", "Tcs Utgst Amount", "Tcs Igst Rate", "Tcs Igst Amount", "Warehouse Id", "Fulfillment Channel", "Payment Method Code", "Bill To City", "Bill To State", "Bill To Country", "Bill To Postalcode", "Customer Bill To Gstid", "Customer Ship To Gstid", "Buyer Name", "Credit Note No", "Credit Note Date", "Irn Number", "Irn Filing Status", "Irn Date", "Irn Error Code"};
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent event) {
        event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        Transferable transferable = event.getTransferable();

        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                java.util.List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

                for (File file : files) {
                    if (file.isFile()) {
                        openFile(file);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        event.dropComplete(true);
    }

    private String transformOrderId(String orderId) {
        return orderId.replace("-", "");
    }

    public void openFile(File file) {
        window.setVisible(false);
        JFrame fileWindow = new JFrame("File Viewer");
        fileWindow.setSize(985, 610);
        fileWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon appIcon = new ImageIcon(APP_ICON_PATH);
        fileWindow.setIconImage(appIcon.getImage());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(backButton).dispose();
            new OrderTracker();
        });
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.add(backButton);
        fileWindow.add(backButtonPanel, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel(getColumnHeaders(), 0);
        JTable fileTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(fileTable);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstRowSkipped = false;
            while ((line = reader.readLine()) != null) {
                if (!firstRowSkipped) {
                    firstRowSkipped = true;
                    continue;
                }
                Vector<String> row = new Vector<>();
                String[] rowData = line.split(",");
                Collections.addAll(row, rowData);
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
        }


        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);


        TableColumnModel columnModel = fileTable.getColumnModel();
        for (int columnIndex = 0; columnIndex < columnModel.getColumnCount(); columnIndex++) {
            TableColumn column = columnModel.getColumn(columnIndex);
            int preferredWidth = 0;
            for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
                TableCellRenderer cellRenderer = fileTable.getCellRenderer(rowIndex, columnIndex);
                Component cellComponent = cellRenderer.getTableCellRendererComponent(fileTable, tableModel.getValueAt(rowIndex, columnIndex), false, false, rowIndex, columnIndex);
                preferredWidth = Math.max(preferredWidth, cellComponent.getPreferredSize().width);
            }
            column.setPreferredWidth(preferredWidth + 20);
        }


        JTextField filterField = new JTextField(20);
        JButton filterButton = new JButton("Filter");
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Enter Order ID: "));
        filterPanel.add(filterField);
        filterPanel.add(filterButton);


        filterButton.addActionListener(e -> {
            String inputOrderId = transformOrderId(filterField.getText()); // Transform the input
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
            fileTable.setRowSorter(sorter);

            sorter.setRowFilter(new RowFilter<TableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                    String fileOrderId = (String) entry.getValue(4); // Assuming the Order ID column is at index 4
                    return transformOrderId(fileOrderId).equals(inputOrderId);
                }
            });
        });

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Enter Shipping ID: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);


        searchButton.addActionListener(e -> {
            String shpmentIdToFilter = searchField.getText();
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
            fileTable.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter(shpmentIdToFilter, 5));
        });

        JPanel filterSearchPanel = new JPanel();
        filterSearchPanel.setLayout(new BoxLayout(filterSearchPanel, BoxLayout.X_AXIS));
        filterSearchPanel.add(filterPanel);
        filterSearchPanel.add(searchPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(filterSearchPanel, BorderLayout.NORTH);

        fileWindow.add(scrollPane, BorderLayout.CENTER);
        fileWindow.add(bottomPanel, BorderLayout.SOUTH);

        fileWindow.setVisible(true);
    }


    public void createDropTarget() {
        DropTarget dropTarget = new DropTarget(window, DnDConstants.ACTION_COPY_OR_MOVE, this);
        window.setDropTarget(dropTarget);

        DropTarget dpTarget = new DropTarget(window, DnDConstants.ACTION_COPY_OR_MOVE, this);
        window.setDropTarget(dpTarget);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}