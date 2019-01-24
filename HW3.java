/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testSwing;



import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HW3 {
	Connection conn = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	
	private JFrame MoviesApplication;
	private JTextField txtGenres,txtCountries,txtLocations,txtCritics,criticsValue,reviewsValue,weightValue;
	private JTextArea resultText;
	private JPanel GenrePanel,CriticsRating,ResultPanel,QueryPanel,dummy;
	private JScrollPane scrollPane,scrollPane_countries,scrollPane_locations,scrollPane_tagValues;
	private JList Genre,Countries,Locations,Ratings,TagValues;
	private JComboBox comboBox,critics,reviews,tagWeight;    
	private JDateChooser yearFrom,yearTo;
	private JTable resultTable;
	private JButton ExecuteQueryHere;
	private String betwnAttr = "",relation="",val="",relation2 ="",val2 = "";
	private String yearto="",yearfrom="";
	public String resultString;

	/**
	 * Launch the application.
	 */
	ArrayList<String> genreList = new ArrayList<>();
	ArrayList<String> countryList = new ArrayList<>();
	ArrayList<String> locationList = new ArrayList<>();
	ArrayList<String> ratingList = new ArrayList<>();
	ArrayList<String> tagList = new ArrayList<>();
	ArrayList<String> tagFinalList = new ArrayList<>();
	Map<String, String> tagMap = new HashMap<>();
	HashMap<String, ArrayList<String>> movieMapFinal = new HashMap<>();

	// I am storing the arraylist from the data base in the default list - will display and select from here.
	DefaultListModel genreListdefault = new DefaultListModel<>();
	DefaultListModel  countryListdefault = new DefaultListModel<>();
	DefaultListModel  locationsListdefault = new DefaultListModel<>();
	DefaultListModel ratingsListdefault = new DefaultListModel<>();
	DefaultListModel tagListdefault = new DefaultListModel();
	DefaultTableModel finalResultTable = new DefaultTableModel();
	DefaultTableModel resultModel = new DefaultTableModel();
	String sql2="",location="";
	String relation3 = "";
	String val3 = "";
	
	// all the selected(checked on the list) genres will be stored in the arraylist below 
	ArrayList<String> selectedGenre =  new ArrayList<>();
	ArrayList<String> selectedCountries = new ArrayList<>();
	ArrayList<String> selectedLocations = new ArrayList<>();
	ArrayList<String> selectedRating = new ArrayList<>();
	ArrayList<String> selectedTagValues = new ArrayList<>();
	ArrayList<String> movieIDList = new ArrayList<>();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HW3 window = new HW3();
					window.MoviesApplication.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public HW3() throws ClassNotFoundException,SQLException {
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setBounds(100, 100, 450, 300);
//		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
	initialize();	
	}

		private void initialize()throws ClassNotFoundException,SQLException{
			MoviesApplication = new JFrame();
			MoviesApplication.setTitle("MOVIES APPLICATION");
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			MoviesApplication.setSize(screenSize.width,screenSize.height);
			MoviesApplication.setVisible(true);
			MoviesApplication.setResizable(true);
			MoviesApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			MoviesApplication.getContentPane().setLayout(null);
			txtGenres = new JTextField();
			txtGenres.setFont(new Font("Tahoma",Font.BOLD,20));
			txtGenres.setBackground(Color.LIGHT_GRAY);
			txtGenres.setText("         GENRES");
			txtGenres.setBounds(0,29,232,30);
			MoviesApplication.getContentPane().add(txtGenres, BorderLayout.NORTH);
			txtGenres.setColumns(10);
			
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/orcl", "scott", "tiger");
			String sql1 = "Select DISTINCT (genre) from movie_genres ";
			stmt = conn.prepareStatement(sql1);
			rs=stmt.executeQuery();
			
			while(rs.next()) {
				String genre = rs.getString(1);
				genreList.add(genre);
			}
			
			stmt.close();
			rs.close();
					GenrePanel = new JPanel();
					GenrePanel.setBounds(0,50,232,400);
					MoviesApplication.getContentPane().add(GenrePanel);
					GenrePanel.setLayout(null);
					
					
					scrollPane = new JScrollPane();
					scrollPane.setBounds(0,0,232,400);
					GenrePanel.add(scrollPane);
					
					Genre = new JList(genreListdefault);
					Genre.setCellRenderer(new CheckListRenderer());
					scrollPane.setViewportView(Genre);
					Genre.setFont(new Font("Tahoma",Font.PLAIN,15));
					Genre.setBorder(new LineBorder(new Color(0,0,0),4));
					Genre.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					for( int i =0;i<genreList.size();i++) {
						genreListdefault.addElement(new CheckListItem(genreList.get(i)));
					}
					scrollPane_countries =  new JScrollPane();
					scrollPane_countries.setBounds(236,50,232,400);
					MoviesApplication.getContentPane().add(scrollPane_countries);

					//setting the text "Countries" on top
					txtCountries = new JTextField();
					txtCountries.setFont(new Font("Tahoma",Font.BOLD,20));
					txtCountries.setBackground(Color.LIGHT_GRAY);
					txtCountries.setText("         Countries");
					txtCountries.setBounds(232,29,232,30);
					MoviesApplication.getContentPane().add(txtCountries, BorderLayout.NORTH);
					txtCountries.setColumns(10);

					Countries = new JList(countryListdefault);
					Countries.setCellRenderer(new CheckListRenderer());
					scrollPane_countries.setViewportView(Countries);
					Countries.setFont(new Font("Tahoma",Font.PLAIN,15));
					Countries.setBorder(new LineBorder(new Color(0,0,0),4));
					Countries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// adding the panel where query will be visible. Has button and its click performance

			QueryPanel = new JPanel();
			QueryPanel.setBounds(0,550,700,250);
			QueryPanel.setBorder(new LineBorder(new Color(0,0,0),4));
			ExecuteQueryHere = new JButton("Execute Query");
			resultText = new JTextArea();
			resultText.setColumns(35);
			resultText.setFont(new Font("Tahoma",Font.BOLD,10));
                                                        resultText.setLineWrap(true);
			resultText.setBackground(Color.WHITE);
			ExecuteQueryHere.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					// basically here we will have a sql query fired which will inturn give a list of strings.
//					System.out.println("Click of the mouse is working");
//					resultText.setText("Click is working perfectly");
//					String[] rowobj = new String[3];						//hardcoding the data to be populated
//					String abc = "1234";
//					String bcd = "5678";
//					String def = "6789";
//					rowobj = new String[] {abc,bcd,def};
//					finalResultTable.addRow(rowobj);
                                                                                            finalResultTable.setRowCount(0);
					resultText.setText("");

					resultModel = (DefaultTableModel) resultTable.getModel();
                                        
					while(resultModel.getRowCount()>0){
						resultModel.removeRow(0);
					}
					
					betwnAttr = "INTERSECT";
					relation2="";
					if(comboBox.getSelectedIndex()==1)
					{
					   betwnAttr = "INTERSECT";
					}
					else
					{
					   if(comboBox.getSelectedIndex()==0 || comboBox.getSelectedIndex()==2)
					   {
					      betwnAttr = "UNION";
					   }
					}
					if(reviews.getSelectedIndex()==1)
					{
					   relation2 = ">";
					   val2 = reviewsValue.getText().trim();
					}
					else
					{
					   if(reviews.getSelectedIndex()==2)
					   {
					      relation2 = "<";
					      val2 = reviewsValue.getText().trim();
					   }
					   if(reviews.getSelectedIndex()==0)
					   {
					      relation2 = "=";
					      val2 = reviewsValue.getText().trim();
					   }

					}
					
					if(tagWeight.getSelectedIndex()==1)
					{
					   relation3 = ">";
					   val3 = weightValue.getText().trim();
					}
					else
					{
					   if(tagWeight.getSelectedIndex()==2)
					   {
					      relation3 = "<";
					      val3 = weightValue.getText().trim();
					   }
					   if(tagWeight.getSelectedIndex()==0)
					   {
					      relation3 = "=";
					      val3 = weightValue.getText().trim();
					   }

					}
					
					movieIDList.clear();
					movieMapFinal.clear();
					
					movieIDList = queryMovieFinal(selectedGenre, selectedCountries, selectedLocations, selectedTagValues, relation, val, relation2, val2, yearto, yearfrom, relation3, val3, betwnAttr);
					System.out.println("TagValues size" + tagList.size());
                                                                                               System.out.println("NumberofMovies:" +movieIDList.size());
					resultText.setText(resultString);

				}
			});
			//QueryPanel.add(ExecuteQueryHere,BorderLayout.SOUTH);
			ExecuteQueryHere.setBounds(150, 455,200, 50);

			MoviesApplication.getContentPane().add(ExecuteQueryHere);
			MoviesApplication.getContentPane().add(QueryPanel);


			//resultText = new JTextField();
			//JTextArea resultText = new JTextArea();
			//resultText.setHorizontalAlignment(SwingConstants.RIGHT);
			
			//resultText.setText("        ");
			//resultText.setBounds(100,520,300,300);
			// code for queryPanle till here

			finalResultTable = new DefaultTableModel();
			finalResultTable.addColumn("Title");
			finalResultTable.addColumn("Year");
			finalResultTable.addColumn("Country");
			finalResultTable.addColumn("AvgRating");
			finalResultTable.addColumn("AvgNumReviews");
			finalResultTable.addColumn("Genre");
			finalResultTable.addColumn("Location");
			
// code for the result panel and the Resulttable

			ResultPanel = new JPanel();
			ResultPanel.setBounds(700,430,700,450);
			ResultPanel.setBorder(new LineBorder(new Color(0,0,0),4));
			QueryPanel.setLayout(new GridLayout(0, 1, 0, 0));
			QueryPanel.add(new JScrollPane(resultText));
			MoviesApplication.getContentPane().add(ResultPanel);
			ResultPanel.setLayout(null);

			JScrollPane scrollPane_results = new JScrollPane();
			scrollPane_results.setBounds(10,10,650,400);
			ResultPanel.add(scrollPane_results);
			resultTable = new JTable();
			scrollPane_results.setViewportView(resultTable);
			resultTable.setModel(finalResultTable);
			resultTable.setBorder(new LineBorder(new Color(0,0,0)));


			// resultPanle code ends here

					comboBox = new JComboBox();
					comboBox.setFont(new Font("Times New Roman", Font.PLAIN, 15));
					comboBox.setBounds(5, 455, 100, 50);
					comboBox.setMaximumRowCount(3);
					comboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select AND, OR between attributes", "AND", "OR" }));
					MoviesApplication.getContentPane().add(comboBox);

					txtLocations = new JTextField();
					txtLocations.setFont(new Font("Tahoma",Font.BOLD,20));
					txtLocations.setBackground(Color.LIGHT_GRAY);
					txtLocations.setText("         Locations");
					txtLocations.setBounds(472,29,232,30);
					MoviesApplication.getContentPane().add(txtLocations, BorderLayout.NORTH);
					txtCountries.setColumns(10);
					scrollPane_locations =  new JScrollPane();
					scrollPane_locations.setBounds(472,50,232,400);
					MoviesApplication.getContentPane().add(scrollPane_locations);


					Locations = new JList(locationsListdefault);
					Locations.setCellRenderer(new CheckListRenderer());
					scrollPane_locations.setViewportView(Locations);
					Locations.setFont(new Font("Tahoma",Font.PLAIN,15));
					Locations.setBorder(new LineBorder(new Color(0,0,0),4));
					Locations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					
					
					
					
					
			CriticsRating = new JPanel();
			CriticsRating.setBounds(710,67,225,300);
			//CriticsRating.setBorder("CriticsRating");
			CriticsRating.setBorder(new LineBorder(new Color(0,0,0),4));
			txtCritics = new JTextField();
			txtCritics.setFont(new Font("Tahoma",Font.BOLD,20));
			txtCritics.setBackground(Color.LIGHT_GRAY);
			txtCritics.setText("         Critics Rating");
			txtCritics.setBounds(710,29,225,30);
			MoviesApplication.getContentPane().add(txtCritics);

			MoviesApplication.getContentPane().add(CriticsRating);


			critics = new JComboBox();
			CriticsRating.add(critics);
			critics.setModel(new DefaultComboBoxModel(new String[]{"Choose Rating","=","<",">"}));
			criticsValue = new JTextField();
			criticsValue.setColumns(10);
			criticsValue.setBounds(900,200,132,27);
			criticsValue.setText("       ");
			CriticsRating.add(criticsValue);
//			critics.addMouseListener(new MouseAdapter() {
//				@Override
//				public void mouseClicked(MouseEvent e) {
//					tagMap.clear();
//					tagList.clear();
//					tagList = queryMovieTags(genreList,countryList,locationList,relation,val,relation2,val2,yearfrom,yearto,betwnAttr);
//				}
//			});
			Action action = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					betwnAttr = " INTERSECT ";
					relation2="";
					if(comboBox.getSelectedIndex()==1)
					{
					   betwnAttr = " INTERSECT ";
					}
					else
					{
					   if(comboBox.getSelectedIndex()==0 || comboBox.getSelectedIndex()==2)
					   {
					      betwnAttr = " UNION ";
					   }
					}
					if(reviews.getSelectedIndex()==1)
					{
					   relation2 = " > ";
					   val2 = reviewsValue.getText().trim();
					}
					else
					{
					   if(reviews.getSelectedIndex()==2)
					   {
					      relation2 = " < ";
					      val2 = reviewsValue.getText().trim();
					   }
					   if(reviews.getSelectedIndex()==0)
					   {
					      relation2 = " = ";
					      val2 = reviewsValue.getText().trim();
					   }

					}
					tagMap.clear();
					tagList.clear();
					tagListdefault.clear();
                                                                                              selectedTagValues.clear();
					tagList = queryMovieTags(selectedGenre, selectedCountries, selectedLocations,relation,val,relation2,val2,yearfrom,yearto,betwnAttr);
					System.out.println("reviewsValue " + reviewsValue.getText().trim());
					System.out.println("reviewsValue " + criticsValue.getText().trim());

				}
			};
			criticsValue.addActionListener(action);


			reviews = new JComboBox();
			CriticsRating.add(reviews);
			reviews.setModel(new DefaultComboBoxModel(new String[]{"Number Of Reviews","=","<",">"}));
			reviewsValue = new JTextField();
			reviewsValue.setColumns(10);
			reviewsValue.setBounds(900,300,132,27);
			reviewsValue.setText("       ");
			CriticsRating.add(reviewsValue);
//			reviews.addMouseListener(new MouseAdapter() {
//				@Override
//				public void mouseClicked(MouseEvent e) {
//					tagMap.clear();
//					tagList.clear();
//					tagList = queryMovieTags(genreList,countryList,locationList,relation,val,relation2,val2,yearfrom,yearto,betwnAttr);
//				}
//			});

			reviewsValue.addActionListener(action);




			tagWeight = new JComboBox();
			CriticsRating.add(tagWeight);
			tagWeight.setModel(new DefaultComboBoxModel(new String[]{"Choose Tag Weight","=","<",">"}));
			weightValue = new JTextField();
			weightValue.setColumns(10);
			weightValue.setBounds(900,200,132,27);
			weightValue.setText("       ");
			CriticsRating.add(weightValue);
			weightValue.addActionListener(action);


			yearFrom = new JDateChooser();
			yearFrom.getCalendarButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			yearFrom.getCalendarButton().setText("From Year");
			CriticsRating.add(yearFrom);
			yearTo = new JDateChooser();
			yearTo.getCalendarButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String toYear = ((JTextField)yearTo.getDateEditor().getUiComponent()).getText();
					System.out.println(toYear);
				}
			});
			yearTo.getCalendarButton().setText("To Year");
			CriticsRating.add(yearTo);
			String fromYear =((JTextField)yearFrom.getDateEditor().getUiComponent()).getText() ;

			yearTo.addPropertyChangeListener(new PropertyChangeListener(){


				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					// TODO Auto-generated method stub
					String dateTo = (((JTextField) yearTo.getDateEditor().getUiComponent()).getText());
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					//String s = sdf.format(date);
					//String []D=s.split("-");
					//yr = D[1];

					yearto = dateTo == null || dateTo.isEmpty()?"": dateTo.split(",")[1];
//					System.out.println(yearTo);
					tagMap.clear();
					tagList.clear();
					tagListdefault.clear();
                                                                                             selectedTagValues.clear();
					tagList = queryMovieTags(selectedGenre, selectedCountries, selectedLocations,relation,val,relation2,val2,yearfrom,yearto,betwnAttr);
				}

			});

			yearFrom.addPropertyChangeListener(new PropertyChangeListener(){


				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					// TODO Auto-generated method stub
					String dateFrom = (((JTextField) yearFrom.getDateEditor().getUiComponent()).getText());
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					//String s = sdf.format(date);
					//String []D=s.split("-");
					//yr = D[1];

					yearfrom = dateFrom == null || dateFrom.isEmpty()?"": dateFrom.split(",")[1];
//					System.out.println(yearFrom);
					tagMap.clear();
					tagList.clear();
					tagListdefault.clear();
                                                                                               selectedTagValues.clear();
					tagList = queryMovieTags(selectedGenre, selectedCountries, selectedLocations,relation,val,relation2,val2,yearfrom,yearto,betwnAttr);
				}
			});

//			JButton b=new JButton("Click Here");
//			b.setBounds(1000,100,95,30);
//			MoviesApplication.add(b);
//			MoviesApplication.setSize(400,400);
//			b.addMouseListener(new MouseAdapter() {
//				@Override
//				public void mouseClicked(MouseEvent e) {
//					System.out.println("Critics Value " + criticsValue.getText().trim());
//					System.out.println("Reviews Value " +reviewsValue.getText().trim());
//				}
//			});


			scrollPane_tagValues = new JScrollPane();
			scrollPane_tagValues.setBounds(950,50,550,350);
			MoviesApplication.getContentPane().add(scrollPane_tagValues);

			txtCountries = new JTextField();
			txtCountries.setFont(new Font("Tahoma",Font.BOLD,20));
			txtCountries.setBackground(Color.LIGHT_GRAY);
			txtCountries.setText("         Movie Tag Values");
			txtCountries.setBounds(950,29,550,30);
			MoviesApplication.getContentPane().add(txtCountries, BorderLayout.NORTH);
			txtCountries.setColumns(10);
			TagValues = new JList(tagListdefault);
			TagValues.setCellRenderer(new CheckListRenderer());
			scrollPane_tagValues.setViewportView(TagValues);
			TagValues.setFont(new Font("Tahoma",Font.PLAIN,15));
			TagValues.setBorder(new LineBorder(new Color(0,0,0),4));
			TagValues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

// calling function for checking the boxes and populating the corresponding scrolling panel
				checkGenres();
				checkCountry();
				checkLocation();
				checkTags();



					
		}
		
		public void checkGenres() {
			Genre.addMouseListener(new MouseAdapter(){
				public void mouseClicked (MouseEvent e ) {
				// here i have to clear the already selected countries list
					countryListdefault.clear();
					locationsListdefault.clear();
                                        //Selected values - selectedgenre
					genreList.clear();
					countryList.clear();
					locationList.clear();
					tagMap.clear();
					tagList.clear();
					tagListdefault.clear();
                                                                                             selectedCountries.clear(); 
                                                                                             selectedLocations.clear();
                                                                                             selectedTagValues.clear();
					
			int index = Genre.locationToIndex(e.getPoint());
				CheckListItem item = (CheckListItem)Genre.getModel().getElementAt(index);
				if(item.isSelected()) {
					item.isSelected =false;
					item.setSelected(item.isSelected);
					//remove the selected
					selectedGenre.remove(item.toString());}
				else { item.setSelected(!item.isSelected());
				item.isSelected = true;}
			// repainting the checked and unchecked items
				Genre.repaint(Genre.getCellBounds(index, index));
					if(selectedGenre.size()==0) {
						countryListdefault.clear();
					}
					if(item.isSelected()) {
						selectedGenre.add(item.toString());
					}
					
					if(selectedGenre.size()!=0) {
						countryListdefault.clear();
						String genreList_String = null;
						if(selectedGenre.size()==1) {
							genreList_String = "('"+selectedGenre.get(0)+"')";
							genreList.add(selectedGenre.get(0));
							System.out.println(genreList);
						}
						else if(selectedGenre.size()>1) {
							genreList_String = "('"+selectedGenre.get(0)+"'";
							genreList.add(selectedGenre.get(0));
							for(int k =1;k<selectedGenre.size();k++) {
								genreList_String+=",'"+selectedGenre.get(k)+"'";
								genreList.add(selectedGenre.get(k));

							}
							genreList_String+=")";
							System.out.println(genreList_String);

							System.out.println("genreList " + genreList);

						}
		 sql2 = "Select Distinct(m1.COUNTRY)from movie_countries m1,movie_genres m2 where( m1.MOVIEID=m2.MOVIEID and m2.genre IN" + genreList_String+")";
						System.out.print(sql2);
						try {
							stmt = conn.prepareStatement(sql2);
							rs = stmt.executeQuery();
							while(rs.next()) {
								String country = rs.getString(1);
								countryList.add(country);
								countryListdefault.addElement(new CheckListItem(country));
							}
							
							stmt.close();
							rs.close();
							Countries.setModel(countryListdefault);
                                                                                                                                   tagList = queryMovieTags(selectedGenre, selectedCountries, selectedLocations,relation,val,relation2,val2,yearfrom,yearto,betwnAttr);
						} catch(SQLException e1) {
							e1.printStackTrace();
						}
						
					}
				
					// create all variables for the subcategory and then finish this part of code 
				}
				// create a panel for countries and then finish this code 
			});
		}

		public void checkCountry() {
		betwnAttr = "";
			if(comboBox.getSelectedIndex()==1)
			{
				betwnAttr = " INTERSECT ";
			}
			else
			{
				if(comboBox.getSelectedIndex()==0 || comboBox.getSelectedIndex()==2)
				{
					betwnAttr = " UNION ";
				}
			}

			Countries.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					locationsListdefault.clear();
					countryList.clear();
					locationList.clear();
					tagMap.clear();
					tagList.clear();
					tagListdefault.clear(); 
                                                                                             selectedLocations.clear();
                                                                                             selectedTagValues.clear();
					
					int index2 = Countries.locationToIndex(e.getPoint());
					CheckListItem item2 = (CheckListItem) Countries.getModel().getElementAt(index2);
					if(item2.isSelected()) {
						item2.isSelected =false;
						item2.setSelected(item2.isSelected);
						//remove the selected
						selectedCountries.remove(item2.toString());
					} else {
						item2.setSelected(!item2.isSelected());
						item2.isSelected = true;
					}
					Countries.repaint(Countries.getCellBounds(index2, index2));
					if(selectedCountries.size()==0) {
						locationsListdefault.clear();
						locationList.clear();
					}
					if(item2.isSelected()) {
						selectedCountries.add(item2.toString());
					}

					if(selectedCountries.size()!=0){
						locationsListdefault.clear();

						if(selectedCountries.size()==1) {
							countryList.add(selectedCountries.get(0));

						}
						else if(selectedCountries.size()>1) {
							countryList.add(selectedCountries.get(0));
							for(int k =1;k<selectedCountries.size();k++) {
								countryList.add(selectedCountries.get(k));

							}
//							System.out.println(countryList.toString());
						}

						locationList = queryMovieLocation(selectedGenre, selectedCountries,betwnAttr);
						Locations.setModel(locationsListdefault);
                                                                                                                tagList = queryMovieTags(selectedGenre, selectedCountries, selectedLocations,relation,val,relation2,val2,yearfrom,yearto,betwnAttr);
					}
//					if(countryList.size() != 0)
//					{
//						int start = 0;
//						int end = Countries.getModel().getSize()-1;
//						Countries.setSelectionInterval(start, end);
//						countryList =  Countries.getSelectedValuesList();
//						//countryJList.addMouseListener(mouseListener);
//					}

				}
			});

		}

		public void checkLocation() {

			betwnAttr = "";
			if(comboBox.getSelectedIndex()==1)
			{
				betwnAttr = " INTERSECT ";
			}
			else
			{
				if(comboBox.getSelectedIndex()==0 || comboBox.getSelectedIndex()==2)
				{
					betwnAttr = " UNION ";
				}
			}
			Locations.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
//					locationsListdefault.clear();

					locationList.clear();
					ratingList.clear();
					tagMap.clear();
					tagList.clear();
					tagListdefault.clear();
					
					int index3 = Locations.locationToIndex(e.getPoint());
					CheckListItem item3 = (CheckListItem) Locations.getModel().getElementAt(index3);
					if(item3.isSelected()) {
						item3.isSelected =false;
						item3.setSelected(item3.isSelected);
						//remove the selected
						selectedLocations.remove(item3.toString());
					} else {
						item3.setSelected(!item3.isSelected());
						item3.isSelected = true;
					}
					Countries.repaint(Countries.getCellBounds(index3, index3));
					if(selectedLocations.size()==0) {
//						ratingsListdefault.clear();
					}
					if(item3.isSelected()) {
						selectedLocations.add(item3.toString());
					}

					if(selectedLocations.size()!=0){
//						ratingsListdefault.clear();

						if(selectedLocations.size()==1) {
							locationList.add(selectedLocations.get(0));

						}
						else if(selectedLocations.size()>1) {
							locationList.add(selectedLocations.get(0));
							for(int k =1;k<selectedLocations.size();k++) {
								locationList.add(selectedLocations.get(k));

							}


						}

//						System.out.println("LOCATION IN CHECK LOCATION " + locationList.toString());
//						ratingList = queryMovieRating(genreList,countryList,betwnAttr);
//						Ratings.setModel(ratingsListdefault);
					}
//					if(countryList.size() != 0)
//					{
//						int start = 0;
//						int end = Countries.getModel().getSize()-1;
//						Countries.setSelectionInterval(start, end);
//						countryList =  Countries.getSelectedValuesList();
//						//countryJList.addMouseListener(mouseListener);
//					}
                                                                                            selectedTagValues.clear();
                                                                                            tagList = queryMovieTags(selectedGenre, selectedCountries, selectedLocations,relation,val,relation2,val2,yearfrom,yearto,betwnAttr);
				}
			});

			}

		
		public void checkTags() {

			betwnAttr = "";
			if(comboBox.getSelectedIndex()==1)
			{
				betwnAttr = " INTERSECT ";
			}
			else
			{
				if(comboBox.getSelectedIndex()==0 || comboBox.getSelectedIndex()==2)
				{
					betwnAttr = " UNION ";
				}
			}
			TagValues.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
//					locationsListdefault.clear();
					tagFinalList.clear();
					
					int index4 = TagValues.locationToIndex(e.getPoint());
					CheckListItem item4 = (CheckListItem) TagValues.getModel().getElementAt(index4);
					if(item4.isSelected()) {
						item4.isSelected =false;
						item4.setSelected(item4.isSelected);
						//remove the selected
						selectedTagValues.remove(item4.toString());
					} else {
						item4.setSelected(!item4.isSelected());
						item4.isSelected = true;
					}
					TagValues.repaint(TagValues.getCellBounds(index4, index4));
					if(selectedTagValues.size()==0) {
//						ratingsListdefault.clear();
					}
					if(item4.isSelected()) {
						selectedTagValues.add(item4.toString());
					}

					if(selectedTagValues.size()!=0){
//						ratingsListdefault.clear();

						if(selectedTagValues.size()==1) {
							tagFinalList.add(tagMap.get(selectedTagValues.get(0)));

						}
						else if(selectedTagValues.size()>1) {
							tagFinalList.add(tagMap.get(selectedTagValues.get(0)));
							for(int k =1;k<selectedTagValues.size();k++) {
								tagFinalList.add(tagMap.get(selectedTagValues.get(k)));
							}


						}
                                                                                                                
//						System.out.println("tag Value List Final is " + tagFinalList.toString());
					}

				}
			});

			}

		
		
		
			public void checkRating() {
				betwnAttr = "";
				relation="";
				val = "";
				if(comboBox.getSelectedIndex()==1)
				{
					betwnAttr = "INTERSECT";
				}
				else
				{
					if(comboBox.getSelectedIndex()==0 || comboBox.getSelectedIndex()==2)
					{
						betwnAttr = "UNION";
					}
				}
				if(critics.getSelectedIndex()==1)
				{
					relation = ">";
					val = criticsValue.getText().trim();
				}
				else
				{
					if(critics.getSelectedIndex()==2)
					{
						relation = "<";
						val = criticsValue.getText().trim();
					}
					if(critics.getSelectedIndex()==0)
					{
						relation = "=";
						val = criticsValue.getText().trim();
					}

				}
//				System.out.println("val " +val);
				checkReviews();
			}

	public void checkReviews() {
		betwnAttr = "INTERSECT";
		relation2="";
		if(comboBox.getSelectedIndex()==1)
		{
			betwnAttr = "INTERSECT";
		}
		else
		{
			if(comboBox.getSelectedIndex()==0 || comboBox.getSelectedIndex()==2)
			{
				betwnAttr = "UNION";
			}
		}
		if(reviews.getSelectedIndex()==1)
		{
			relation2 = ">";
			val2 = reviewsValue.getText().trim();
		}
		else
		{
			if(reviews.getSelectedIndex()==2)
			{
				relation2 = "<";
				val2 = reviewsValue.getText().trim();
			}
			if(reviews.getSelectedIndex()==0)
			{
				relation2 = "=";
				val2 = reviewsValue.getText().trim();
			}

		}
//		System.out.println("\n val2 " +val2);
	}



	static class CheckListItem{
			private String label;
			private boolean isSelected;
			
			public CheckListItem(String label) {
				this.label = label;
				isSelected = false;
			}
			
			public void setSelected(boolean isSelected) {
				this.isSelected = isSelected;
			}
			public boolean isSelected() {
				return isSelected;
			}
			public String toString() {
				return label;
			}
		}
		
		static class CheckListRenderer extends JCheckBox implements ListCellRenderer {
			public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean hasFocus) {
			setEnabled(list.isEnabled());
			setSelected(((CheckListItem)value).isSelected());
			setFont(list.getFont());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setText(value.toString());
			return this;
			}
		}

	public ArrayList<String> queryMovieLocation(ArrayList<String> genre, ArrayList<String> countries, String betwnAttr){
		ArrayList<String> movieLocations = new ArrayList<>();


		String mainQuery = "SELECT DISTINCT ML.location1 FROM movie_genres MG, movie_locations ML, movie_countries MC WHERE ML.movieID = MC.movieID AND MC.movieID = MG.movieID AND MG.genre LIKE ";


		String query = mainQuery;
		String subQuery = " AND MC.country LIKE ";



		for(int i=0; i<genre.size(); i++){
			for(int j=0; j<countries.size(); j++){
				if(i==genre.size()-1 && j==countries.size()-1){
					query = query + "'" + genre.get(i) + "'" + subQuery + "'" + countries.get(j) + "'";
//					System.out.println("MovieLocationQuery: " + query);
				} else{
					query = query + "'" + genre.get(i) + "'" + subQuery + "'" + countries.get(j) + "'" + " " + betwnAttr + " " + mainQuery;

				}
			}
		}
		try {
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();
			System.out.print("meta " + meta);
			while(rs.next()) {
				for(int i =1;i<=meta.getColumnCount();i++) {
					movieLocations.add(rs.getString(i));
					locationsListdefault.addElement(new CheckListItem(rs.getString(i)));

				}
			}
			stmt.close();
			rs.close();


		}catch (SQLException e1 ){
			e1.printStackTrace();
		}

//		System.out.println("THE GENRE ARE: " + genre);
//		System.out.println("THE Countries ARE: " + countries);
//		System.out.println("THE MOVIE LOCATIONS ARE: " + movieLocations);

		return movieLocations;
	}


public ArrayList<String> queryMovieTags(ArrayList<String> genre, ArrayList<String> countries, ArrayList<String> movieLocations, String ratingSym, String ratingVal, String reviewSym, String reviewVal, String fromYear, String toYear, String betwnAttr) {
		ArrayList<String> tagIDList = new ArrayList<>();
		String mainQuery, selectQuery, fromQuery, whereQuery, query="";


		if (!genre.isEmpty() && !countries.isEmpty() && !movieLocations.isEmpty()) {


			selectQuery = " SELECT DISTINCT T.tagID, T.tag_text ";
			fromQuery = " FROM movie_genres MG, movie_locations ML, movie_countries MC, movie_tags MT, tags T ";
			whereQuery = " WHERE MG.movieID=ML.movieID AND ML.movieID=MC.movieID AND MG.movieID=MT.movieID AND MT.tagID=T.tagID ";


			if ((!ratingSym.isEmpty() && !ratingVal.isEmpty()) || (!reviewSym.isEmpty() && !reviewVal.isEmpty()) || (!fromYear.isEmpty() && !toYear.isEmpty())) {
				fromQuery += ", movies M ";
				whereQuery += " AND M.mID=MG.movieID ";
				if (!ratingSym.isEmpty() && !ratingVal.isEmpty()) {
					whereQuery += " AND M.rtAllCriticsRating " + ratingSym + " '" + ratingVal + "' ";
				}
				if (!reviewSym.isEmpty() && !reviewVal.isEmpty()) {
					whereQuery += " AND M.rtAllCriticsNumReviews " + reviewSym + " '" + reviewVal + "' ";
				}
				if (!fromYear.isEmpty() && !toYear.isEmpty()) {
					whereQuery += " AND M.year > " + fromYear + " AND M.year < " + toYear + " ";
				}
			}


			query = selectQuery + fromQuery + whereQuery + " AND MG.genre LIKE ";
			mainQuery = query;


			String subquery1 = " AND MC.country LIKE ";
			String subquery2 = " AND ML.location1 LIKE ";


			for (int i = 0; i < genre.size(); i++) {
				for (int j = 0; j < countries.size(); j++) {
					for (int k = 0; k < movieLocations.size(); k++) {
						if (i == genre.size() - 1 && j == countries.size() - 1 && k == movieLocations.size() - 1) {
							query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "'" + subquery2 + "'" + movieLocations.get(k) + "'";
						} else {
							query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "'" + subquery2 + "'" + movieLocations.get(k) + "' " + betwnAttr + " " + mainQuery;
						}
					}
				}
			}


			System.out.println(query);
		} else if (!genre.isEmpty() && !countries.isEmpty()) {
			selectQuery = " SELECT DISTINCT T.tagID, T.tag_text ";
			fromQuery = " FROM movie_genres MG, movie_countries MC, movie_tags MT, tags T ";
			whereQuery = " WHERE MG.movieID=MC.movieID AND MG.movieID=MT.movieID AND MT.tagID=T.tagID ";


			if ((!ratingSym.isEmpty() && !ratingVal.isEmpty()) || (!reviewSym.isEmpty() && !reviewVal.isEmpty()) || (!fromYear.isEmpty() && !toYear.isEmpty())) {
				fromQuery += ", movies M ";
				whereQuery += " AND M.mID=MG.movieID ";
				if (!ratingSym.isEmpty() && !ratingVal.isEmpty()) {
					whereQuery += " AND M.rtAllCriticsRating " + ratingSym + " '" + ratingVal + "' ";
				}
				if (!reviewSym.isEmpty() && !reviewVal.isEmpty()) {
					whereQuery += " AND M.rtAllCriticsNumReviews " + reviewSym + " '" + reviewVal + "' ";
				}
				if (!fromYear.isEmpty() && !toYear.isEmpty()) {
					whereQuery += " AND M.year > " + fromYear + " AND M.year < " + toYear + " ";
				}
			}


			query = selectQuery + fromQuery + whereQuery + " AND MG.genre LIKE ";
			mainQuery = query;


			String subquery1 = " AND MC.country LIKE ";


			for (int i = 0; i < genre.size(); i++) {
				for (int j = 0; j < countries.size(); j++) {
					if (i == genre.size() - 1 && j == countries.size() - 1) {
						query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "'";
					} else {
						query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "' " + betwnAttr +  " " + mainQuery;
					}
				}
			}
		} else if (!genre.isEmpty()) {
			selectQuery = " SELECT DISTINCT T.tagID, T.tag_text ";
			fromQuery = " FROM movie_genres MG, movies M, movie_tags MT, tags T ";
			whereQuery = " WHERE M.mID=MG.movieID AND MG.movieID=MT.movieID AND MT.tagID=T.tagID ";


			if ((!ratingSym.isEmpty() && !ratingVal.isEmpty()) || (!reviewSym.isEmpty() && !reviewVal.isEmpty()) || (!fromYear.isEmpty() && !toYear.isEmpty())) {


				if (!ratingSym.isEmpty() && !ratingVal.isEmpty()) {
					whereQuery += " AND M.rtAllCriticsRating " + ratingSym + " '" + ratingVal + "' ";
				}
				if (!reviewSym.isEmpty() && !reviewVal.isEmpty()) {
					whereQuery += " AND M.rtAllCriticsNumReviews " + reviewSym + " '" + reviewVal + "' ";
				}
				if (!fromYear.isEmpty() && !toYear.isEmpty()) {
					whereQuery += " AND M.year > " + fromYear + " AND M.year < " + toYear + " ";
				}
			}


			query = selectQuery + fromQuery + whereQuery + " AND MG.genre LIKE ";
			mainQuery = query;


			for (int i = 0; i < genre.size(); i++) {
				if (i == genre.size() - 1) {
					query = query + "'" + genre.get(i) + "'";
				} else {
					query = query + "'" + genre.get(i) + "'" + betwnAttr + " " + mainQuery;
				}
			}
		} else{
			query = "SELECT M.mID FROM MOVIES M WHERE M.mID = 1200000000000";
		}

		try {
                                                                    stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();
			int nCol = meta.getColumnCount();
			while(rs.next()) {


				String[] row = new String[2];
				for(int i =1;i<=nCol;i++) {
					row[i-1] = rs.getString(i).toString();
				}
				tagIDList.add(row[0]);
//				ADD THE TAGLISTDEFAULT OVER HERE!!!
				tagMap.put(row[1],row[0]);
				tagListdefault.addElement(new CheckListItem(row[1]));

			}
			TagValues.setModel(tagListdefault);
			stmt.close();
			rs.close();

		}catch (SQLException e1 ){
			e1.printStackTrace();
		}

		System.out.println("THE GENRE ARE: " + genre);
		System.out.println("THE Countries ARE: " + countries);
		System.out.println("THE MOVIE LOCATIONS ARE: " + movieLocations);
		System.out.println(Arrays.asList(tagMap));



		return tagIDList;
	}	
	
	public ArrayList<String> queryMovieFinal(ArrayList<String> genre, ArrayList<String> countries, ArrayList<String> movieLocations, ArrayList<String> tagID, String ratingSym, String ratingVal, String reviewSym, String reviewVal, String toYear, String fromYear, String tagWtSym, String tagWtVal, String betwnAttr){
		ArrayList<String> movieIDList = new ArrayList<>();
		HashMap<String, ArrayList<String>> movieMap = new HashMap<>();
		String mainQuery, selectQuery, fromQuery, whereQuery, query;


		if(!genre.isEmpty() && !countries.isEmpty() && !movieLocations.isEmpty() && !tagID.isEmpty()){
			selectQuery = " SELECT DISTINCT MG.movieID ";
			fromQuery = " FROM movie_genres MG, movie_locations ML, movie_countries MC, movie_tags MT, tags T ";
			whereQuery = " WHERE MG.movieID=ML.movieID AND ML.movieID=MC.movieID AND MC.movieID=MT.movieID AND MT.tagID=T.tagID ";

			if((!ratingSym.isEmpty() && !ratingVal.isEmpty()) || (!reviewSym.isEmpty() && !reviewVal.isEmpty()) || (!fromYear.isEmpty() && !toYear.isEmpty())){
				fromQuery += ", movies M ";
				whereQuery += " AND M.mID=MG.movieID ";
				if(!ratingSym.isEmpty() && !ratingVal.isEmpty()){
					whereQuery += " AND M.rtAllCriticsRating " + ratingSym + " '" + ratingVal + "' ";
				}
				if(!reviewSym.isEmpty() && !reviewVal.isEmpty()){
					whereQuery += " AND M.rtAllCriticsNumReviews " + reviewSym + " '" + reviewVal + "' ";
				}
				if(!fromYear.isEmpty() && !toYear.isEmpty()){
					whereQuery += " AND M.year > " + fromYear + " AND M.year < " + toYear + " ";
				}
			}

			if(!tagWtSym.isEmpty() && !tagWtVal.isEmpty()){
				whereQuery += " AND MT.tagWeight " + tagWtSym + " " + tagWtVal + " ";
			}
			query = selectQuery + fromQuery + whereQuery + " AND MG.genre LIKE " ;
			mainQuery = query;

			String subquery1 = " AND MC.country LIKE ";
			String subquery2 = " AND ML.location1 LIKE ";
			String subquery3 = " AND T.tagID = ";

			for(int i=0; i<genre.size(); i++){
				for(int j=0; j<countries.size(); j++){
					for(int k=0; k<movieLocations.size(); k++){
						for(int l=0; l<tagID.size(); l++){
							if(i==genre.size()-1 && j==countries.size()-1 && k==movieLocations.size()-1 && l==tagID.size()-1){
								query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "'" + subquery2 + "'" + movieLocations.get(k) + "'" + subquery3 + " " + tagID.get(l) + "";
							} else{
								query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "'" + subquery2 + "'" + movieLocations.get(k) + "'" + subquery3 + " " + tagID.get(l) + "" + "\n" + betwnAttr + "\n" + mainQuery;
							}
						}
					}
				}
			}
			System.out.println(query);
		}

		else if(!genre.isEmpty() && !countries.isEmpty() && !movieLocations.isEmpty()){

			selectQuery = " SELECT DISTINCT MG.movieID ";
			fromQuery = " FROM movie_genres MG, movie_locations ML, movie_countries MC ";
			whereQuery = " WHERE MG.movieID=ML.movieID AND ML.movieID=MC.movieID ";

			if((!ratingSym.isEmpty() && !ratingVal.isEmpty()) || (!reviewSym.isEmpty() && !reviewVal.isEmpty()) || (!fromYear.isEmpty() && !toYear.isEmpty())){
				fromQuery += ", movies M ";
				whereQuery += " AND M.mID=MG.movieID ";
				if(!ratingSym.isEmpty() && !ratingVal.isEmpty()){
					whereQuery += " AND M.rtAllCriticsRating " + ratingSym + " '" + ratingVal + "' ";
				}
				if(!reviewSym.isEmpty() && !reviewVal.isEmpty()){
					whereQuery += " AND M.rtAllCriticsNumReviews " + reviewSym + " '" + reviewVal + "' ";
				}
				if(!fromYear.isEmpty() && !toYear.isEmpty()){
					whereQuery += " AND M.year > " + fromYear + " AND M.year < " + toYear + " ";
				}
			}

			if(!tagWtSym.isEmpty() && !tagWtVal.isEmpty()){
				fromQuery += ", movie_tags MT ";
				whereQuery += " AND MG.movieID=MT.movieID AND MT.tagWeight " + tagWtSym + " " + tagWtVal + " ";
			}
			query = selectQuery + fromQuery + whereQuery + " AND MG.genre LIKE " ;
			mainQuery = query;

			String subquery1 = " AND MC.country LIKE ";
			String subquery2 = " AND ML.location1 LIKE ";

			for(int i=0; i<genre.size(); i++){
				for(int j=0; j<countries.size(); j++){
					for(int k=0; k<movieLocations.size(); k++){
						if(i==genre.size()-1 && j==countries.size()-1 && k==movieLocations.size()-1){
							query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "'" + subquery2 + "'" + movieLocations.get(k) + "'";
						} else{
							query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "'" + subquery2 + "'" + movieLocations.get(k)  + "' \n" + betwnAttr +  "\n " +  mainQuery;
						}
					}
				}
			}
		}

		else if(!genre.isEmpty() && !countries.isEmpty()){
			selectQuery = " SELECT DISTINCT MG.movieID ";
			fromQuery = " FROM movie_genres MG, movie_countries MC ";
			whereQuery = " WHERE MG.movieID=MC.movieID ";

			if((!ratingSym.isEmpty() && !ratingVal.isEmpty()) || (!reviewSym.isEmpty() && !reviewVal.isEmpty()) || (!fromYear.isEmpty() && !toYear.isEmpty())){
				fromQuery += ", movies M ";
				whereQuery += " AND M.mID=MG.movieID ";
				if(!ratingSym.isEmpty() && !ratingVal.isEmpty()){
					whereQuery += " AND M.rtAllCriticsRating " + ratingSym + " '" + ratingVal + "' ";
				}
				if(!reviewSym.isEmpty() && !reviewVal.isEmpty()){
					whereQuery += " AND M.rtAllCriticsNumReviews " + reviewSym + " '" + reviewVal + "' ";
				}
				if(!fromYear.isEmpty() && !toYear.isEmpty()){
					whereQuery += " AND M.year > " + fromYear + " AND M.year < " + toYear + " ";
				}
			}

			if(!tagWtSym.isEmpty() && !tagWtVal.isEmpty()){
				fromQuery += ", movie_tags MT ";
				whereQuery += " AND MG.movieID=MT.movieID AND MT.tagWeight " + tagWtSym + " " + tagWtVal + " ";
			}
			query = selectQuery + fromQuery + whereQuery + " AND MG.genre LIKE " ;
			mainQuery = query;

			String subquery1 = " AND MC.country LIKE ";

			for(int i=0; i<genre.size(); i++){
				for(int j=0; j<countries.size(); j++){
					if(i==genre.size()-1 && j==countries.size()-1){
						query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "'" ;
					} else{
						query = query + "'" + genre.get(i) + "'" + subquery1 + "'" + countries.get(j) + "' \n" + betwnAttr +  "\n " + mainQuery;
					}
				}
			}
		}

		else if(!genre.isEmpty()){
			selectQuery = " SELECT DISTINCT MG.movieID ";
			fromQuery = " FROM movie_genres MG, movies M ";
			whereQuery = " WHERE M.mID=MG.movieID ";

			if((!ratingSym.isEmpty() && !ratingVal.isEmpty()) || (!reviewSym.isEmpty() && !reviewVal.isEmpty()) || (!fromYear.isEmpty() && !toYear.isEmpty())){

				if(!ratingSym.isEmpty() && !ratingVal.isEmpty()){
					whereQuery += " AND M.rtAllCriticsRating " + ratingSym + " '" + ratingVal + "' ";
				}
				if(!reviewSym.isEmpty() && !reviewVal.isEmpty()){
					whereQuery += " AND M.rtAllCriticsNumReviews " + reviewSym + " '" + reviewVal + "' ";
				}
				if(!fromYear.isEmpty() && !toYear.isEmpty()){
					whereQuery += " AND M.year > " + fromYear + " AND M.year < " + toYear + " ";
				}
			}

			if(!tagWtSym.isEmpty() && !tagWtVal.isEmpty()){
				fromQuery += ", movie_tags MT ";
				whereQuery += " AND MG.movieID=MT.movieID AND MT.tagWeight " + tagWtSym + " " + tagWtVal + " ";
			}
			query = selectQuery + fromQuery + whereQuery + " AND MG.genre LIKE " ;
			mainQuery = query;

			for(int i=0; i<genre.size(); i++){
				if(i==genre.size()-1){
					query = query + "'" + genre.get(i) + "'" ;
				} else{
					query = query + "'" + genre.get(i) + "' \n" + betwnAttr +  "\n " + mainQuery;
				}
			}
		} else{
			query = " SELECT M.mID FROM MOVIES WHERE M.mID = 1293884984938394";
		}

		System.out.println(" THE MovieId Query is : \n" + query);
		resultString = query;

		try {
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();

			while(rs.next()) {
				for(int i =1;i<=meta.getColumnCount();i++) {
					movieIDList.add(rs.getString(i));
				}
			}
			System.out.println("MovieID List: " + movieIDList);
			stmt.close();
			rs.close();
		}catch (SQLException e1 ){
			e1.printStackTrace();
		}

//  Get Movie Information
		String movieQuery = " SELECT M.mID, M.title, M.year, MC.country, M.rtAllCriticsRating, M.RTTOPCRITICSRATING, M.rtAudienceRating, M.RTTOPCRITICSNUMREVIEWS, M.rtAllCriticsNumReviews, M.RTAUDIENCENUMRATINGS "
				+ "FROM movies M, movie_countries MC WHERE M.mID = MC.movieID AND M.mID = ";
		String mq = movieQuery;
		for(int i=0; i<movieIDList.size(); i++){
			if(i==movieIDList.size()-1){
				mq = mq + movieIDList.get(i) + " ";
			} else {
				mq = mq + movieIDList.get(i) + " " + " UNION " + movieQuery;
			}
		}
		System.out.println("Movie Query: \n" + mq);

		if(!movieIDList.isEmpty()){
			try {
				stmt = conn.prepareStatement(mq);
				rs = stmt.executeQuery(mq);
				ResultSetMetaData meta = rs.getMetaData();
				int nCol = meta.getColumnCount();

				while(rs.next()) {
					ArrayList<String> arr = new ArrayList<>();
					String rowID = "";
					float ratings = 0;
					float numreviews = 0;
					for(int i =1;i<=nCol;i++) {
						if(i==1) {
							rowID = rs.getString(i).toString();

						} else if(i<=4) {
							arr.add(rs.getString(i).toString());
						} else if(i<=7) {
							ratings += Float.parseFloat(rs.getString(i).toString());
						}
						else if(i<=nCol) {
							numreviews += Float.parseFloat(rs.getString(i).toString());
						}
					}

					float avg_ratings = ratings/3;
					float avg_numreviews = numreviews/3;

					arr.add(String.valueOf(avg_ratings));
					arr.add(String.valueOf(avg_numreviews));
//    tagIDList.add(row[0]);
//    ADD THE TAGLISTDEFAULT OVER HERE!!!
//    tagMap.put(row[1],row[0]);
					movieMap.put(rowID, arr);

				}

				stmt.close();
				rs.close();
			}catch (SQLException e1 ){
				e1.printStackTrace();
			}
		}



//  Get Genre Information
		String genreQuery = " SELECT DISTINCT MG.movieID, MG.genre FROM movie_genres MG WHERE MG.movieID = ";

		String gq = genreQuery;
		for(int i=0; i<movieIDList.size(); i++){
			if(i==movieIDList.size()-1){
				gq = gq + movieIDList.get(i) + " ";
			} else{
				gq = gq + movieIDList.get(i) + " " + " UNION " + genreQuery;
			}
		}
		System.out.println("Genre Query: \n" + gq);
		if(!movieIDList.isEmpty()){
			try {
				stmt = conn.prepareStatement(gq);
				rs = stmt.executeQuery(gq);
				ResultSetMetaData meta = rs.getMetaData();

				String Prev = "";
				String genreMovieConcat = "";

				while(rs.next()) {
					ArrayList<String> arr = new ArrayList<>();
					String rowID = rs.getString(1).toString();

					if(rowID.equals(Prev)){
						genreMovieConcat += rs.getString(2).toString() + " ";
					} else {
						if(!Prev.equals("")) {
							arr = movieMap.get(Prev);
							arr.add(genreMovieConcat);
							movieMap.put(Prev, arr);
							genreMovieConcat = "" + rs.getString(2).toString() + " ";
						}
						//ADDED THE ELSE LOOP HERE TAKE A LOOK INTO THIS:::::
						else{
							genreMovieConcat = "" + rs.getString(2).toString() + " ";
						}
					}

					Prev = rowID;
					System.out.println("Genre Movie Concat: " + genreMovieConcat + " ROW ID: " + Prev);
				}
				//ADDED THREE STATEMENTS HERE::: LOOK INTO THIS:::::
				ArrayList<String> arr = movieMap.get(Prev);
				arr.add(genreMovieConcat);
				movieMap.put(Prev, arr);
				// ADDED TILL HERE::::::
				stmt.close();
				rs.close();
			}catch (SQLException e1 ){
				e1.printStackTrace();
			}
		}


		System.out.println(Arrays.asList(movieMap));

//  Get Locations Information
		String locationQuery = " SELECT DISTINCT ML.movieID, ML.location1 FROM movie_locations ML WHERE ML.movieID = ";

		String lq = locationQuery;
		for(int i=0; i<movieIDList.size(); i++){
			if(i==movieIDList.size()-1){
				lq = lq + movieIDList.get(i) + " ";
			} else{
				lq = lq + movieIDList.get(i) + " " + " UNION " + locationQuery;
			}
		}
		System.out.println("Location Query: \n" + lq);

		if(!movieIDList.isEmpty()){
			try {
				stmt = conn.prepareStatement(lq);
				rs = stmt.executeQuery(lq);
				ResultSetMetaData meta = rs.getMetaData();
				String Prev = "";
				String locationMovieConcat = "";
				while(rs.next()) {
					ArrayList<String> arr = new ArrayList<>();
					String rowID = rs.getString(1).toString();

					if(rowID.equals(Prev)){
						locationMovieConcat += rs.getString(2).toString() + " ";
					} else {
						if(!Prev.equals("")) {
							arr = movieMap.get(Prev);
							arr.add(locationMovieConcat);
							movieMap.put(Prev, arr);
							locationMovieConcat = "" + rs.getString(2).toString() + " ";

						}
						//Added ELSE LOOP LOOK INTO THIS:
						else{
							locationMovieConcat = "" + rs.getString(2).toString() + " ";
						}
					}

					Prev = rowID;

					System.out.println("Location Movie Concat: " + locationMovieConcat + " ROW ID: " + Prev);

				}
//				ADDED THREE STATEMENTS HERE!!!
				ArrayList<String> arr = movieMap.get(Prev);
				arr.add(locationMovieConcat);
				movieMap.put(Prev, arr);
				//TILL HERE
				stmt.close();
				rs.close();
			}catch (SQLException e1 ){
				e1.printStackTrace();
			}

		}

		System.out.println(Arrays.asList(movieMap));

		Set<String> keys = movieMap.keySet();

		System.out.println("THE FINAL RESULT IS HERE: ");

		for(String key: keys) {
			ArrayList<String> arr = movieMap.get(key);
			finalResultTable.addRow(arr.toArray());
		}

		return movieIDList;
	}


	}

