import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text. *;
import java.util. *;

public class Auction {
	private static Scanner scanner = new Scanner(System.in);
	private static String username;
	private static Connection conn;

	enum Category {
		ELECTRONICS, 
		BOOKS,
		HOME,
		CLOTHING,
		SPORTINGGOODS,
		OTHERS,
		INVALID,
	}
	enum Condition {
		NEW,
		LIKE_NEW,
		GOOD,
		ACCEPTABLE,
		INVALID
	}
	enum Status {
		AVAILABLE,
		SOLD,
		EXPIRED,
	}
	private static void ExpireItem() {
		try { 
			PreparedStatement stmt = conn.prepareStatement("select item_id, bidder_id, sug_price from items natural left outer join bids where status='AVAILABLE' and closing_date <= now() and is_highest is not false");
			ResultSet rset = stmt.executeQuery();
			while(rset.next()){
				int item = rset.getInt("item_id");
				String bidder = rset.getString("bidder_id");
				int price = rset.getInt("sug_price");
				/* TODO: if bidder exist, charge it him*/
				if(!rset.wasNull()){
					stmt = conn.prepareStatement("update items set status=? where item_id=?");
					stmt.setString(1, Status.SOLD.name());
					stmt.setInt(2, item);
					stmt.executeUpdate();
					stmt = conn.prepareStatement("insert into billings values(?, ?, ?, now())");
					stmt.setInt(1, item);
					stmt.setString(2, bidder);
					stmt.setInt(3, price);
					stmt.executeUpdate();
				}
				/* TODO: if bidder doesnt exist, expire it*/
				else{
					stmt = conn.prepareStatement("update items set status=? where item_id=?");
					stmt.setString(1, Status.EXPIRED.name());
					stmt.setInt(2, item);
					stmt.executeUpdate();
				}
			}
			stmt.close();
		}catch (SQLException e) {
			System.out.println(e);
			return;
		}
		return;
	}

	private static boolean LoginMenu() {
		String userpass, is_admin;

		System.out.print("----< User Login >\n" +
				" ** To go back, enter 'back' in user ID.\n" +
				"     user ID: ");
		try{
			username = scanner.next();
			scanner.nextLine();

			if(username.equalsIgnoreCase("back")){
				return false;
			}

			System.out.print("     password: ");
			userpass = scanner.next();
			scanner.nextLine();
		}catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			username = null;
			return false;
		}

		try { 
			PreparedStatement stmt = conn.prepareStatement("select * from users where user_id=? and password=?"); 
			stmt.setString(1, username);
			stmt.setString(2, userpass);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {  
				/* If Login Fails */
				System.out.println("Error: Incorrect user name or password");
				username = null;
				stmt.close();
				return false; 
			}
			stmt.close();
		}catch (SQLException e) {
			System.out.println(e);
			return false;
		}
		System.out.println("You are successfully logged in.\n");
		return true;
	}

	private static boolean SellMenu() {
		Category category = Category.INVALID;
		Condition condition = Condition.INVALID;
		char choice;
		int price;
		boolean flag_catg = true, flag_cond = true;
		String description;
		LocalDateTime dateTime;
		do{
			System.out.println(
					"----< Sell Item >\n" +
					"---- Choose a category.\n" +
					"    1. Electronics\n" +
					"    2. Books\n" +
					"    3. Home\n" +
					"    4. Clothing\n" +
					"    5. Sporting Goods\n" +
					"    6. Other Categories\n" +
					"    P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
			}catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			flag_catg = true;

			switch ((int) choice){
				case '1':
					category = Category.ELECTRONICS;
					continue;
				case '2':
					category = Category.BOOKS;
					continue;
				case '3':
					category = Category.HOME;
					continue;
				case '4':
					category = Category.CLOTHING;
					continue;
				case '5':
					category = Category.SPORTINGGOODS;
					continue;
				case '6':
					category = Category.OTHERS;
					continue;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_catg = false;
					continue;
			}
		}while(!flag_catg);

		do{
			System.out.println(
					"---- Select the condition of the item to sell.\n" +
					"   1. New\n" +
					"   2. Like-new\n" +
					"   3. Used (Good)\n" +
					"   4. Used (Acceptable)\n" +
					"   P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			}catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			flag_cond = true;

			switch (choice) {
				case '1':
					condition = Condition.NEW;
					break;
				case '2':
					condition = Condition.LIKE_NEW;
					break;
				case '3':
					condition = Condition.GOOD;
					break;
				case '4':
					condition = Condition.ACCEPTABLE;
					break;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_cond = false;
					continue;
			}
		}while(!flag_cond);

		try {
			System.out.println("---- Description of the item (one line): ");
			description = scanner.nextLine();
			System.out.println("---- Buy-It-Now price: ");

			while (!scanner.hasNextInt()) {
				scanner.next();
				System.out.println("Invalid input is entered. Please enter Buy-It-Now price: ");
			}

			price = scanner.nextInt();
			scanner.nextLine();

			System.out.print("---- Bid closing date and time (YYYY-MM-DD HH:MM): ");
			// you may assume users always enter valid date/time
			String date = scanner.nextLine();  /* "2023-03-04 11:30"; */
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			dateTime = LocalDateTime.parse(date, formatter);
		}catch (Exception e) {
			System.out.println("Error: Invalid input is entered. Going back to the previous menu.");
			return false;
		}
		try { 
			PreparedStatement stmt = conn.prepareStatement("insert into items values(DEFAULT,?,?,?,?,?,now(),?,?)");
			stmt.setString(1, username);
			stmt.setString(2, category.name());
			stmt.setString(3, description);
			stmt.setString(4, condition.name());
			stmt.setInt(5, price);
			stmt.setTimestamp(6, Timestamp.valueOf(dateTime));
			stmt.setString(7, Status.AVAILABLE.name());
			stmt.executeUpdate();
			stmt.close();
		}catch (SQLException e) {
			System.out.println(e);
			return false;
		}
		System.out.println("Your item has been successfully listed.\n");
		return true;
	}

	private static boolean SignupMenu() {
		/* 2. Sign Up */
		String new_username, userpass, is_admin;
		System.out.print("----< Sign Up >\n" + " ** To go back, enter 'back' in user ID.\n" +"---- user name: ");
		try {
			new_username = scanner.next();
			scanner.nextLine();
			if(new_username.equalsIgnoreCase("back")){
				return false;
			}
			System.out.print("---- password: ");
			userpass = scanner.next();
			scanner.nextLine();
			System.out.print("---- In this user an administrator? (Y/N): ");
			is_admin = scanner.next();
			scanner.nextLine();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Please select again.");
			return false;
		}

		try { 
			PreparedStatement stmt = conn.prepareStatement("insert into users values(?,?,?)"); 
			stmt.setString(1, new_username);
			stmt.setString(2, userpass);
			stmt.setBoolean(3, is_admin.equalsIgnoreCase("y"));
			stmt.executeUpdate();
			stmt.close();
		}catch (SQLException e) {
			System.out.println(e);
			return false;
		}
		System.out.println("Your account has been successfully created.\n");
		return true;
	}

	private static boolean AdminMenu() {
		/* 3. Login as Administrator */
		char choice;
		String adminname, adminpass;
		String keyword, seller;
		System.out.print("----< Login as Administrator >\n" + " ** To go back, enter 'back' in user ID.\n" + "---- admin ID: ");

		try {
			adminname = scanner.next();
			scanner.nextLine();
			if(adminname.equalsIgnoreCase("back")){
				return false;
			}
			System.out.print("---- password: ");
			adminpass = scanner.nextLine();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			return false;
		} 
		
		try { 
			PreparedStatement stmt = conn.prepareStatement("select * from users where user_id=? and password=? and is_admin=true"); 
			stmt.setString(1, adminname);
			stmt.setString(2, adminpass);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {  
				/* If Login Fails */
				System.out.println("Error: Incorrect admin name or password");
				return false; 
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println(e);
			return false;
		}

		do {
			System.out.println(
					"----< Admin menu > \n" +
					"    1. Print Sold Items per Category \n" +
					"    2. Print Account Balance for Seller \n" +
					"    3. Print Seller Ranking \n" +
					"    4. Print Buyer Ranking \n" +
					"    P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			if (choice == '1') {
				System.out.println("----Enter Category to search : ");
				keyword = scanner.next();
				scanner.nextLine();
				/*TODO: Print Sold Items per Category */
				System.out.println("sold item       | sold date       | seller ID   | buyer ID   | price | commissions");
				System.out.println("----------------------------------------------------------------------------------");

				try { 
					PreparedStatement stmt = conn.prepareStatement("select item_id, sold_date, seller_id, buyer_id, payment, payment*0.1 as commissions from billings natural join items where category=?"); 
					stmt.setString(1, keyword);
					ResultSet rset = stmt.executeQuery();
					while(rset.next()){
						System.out.println(String.format("%-17s %-17s %-13s %-12s %-7d %-12d", rset.getString("item_id"), rset.getDate("sold_date"), rset.getString("seller_id"), rset.getString("buyer_id"), rset.getInt("payment"), rset.getInt("commissions")));
					}
					stmt.close();
				}catch (SQLException e) {
					System.out.println(e);
					return false;
				}
				continue;
			} else if (choice == '2') {
				/*TODO: Print Account Balance for Seller */
				System.out.println("---- Enter Seller ID to search : ");
				seller = scanner.next();
				scanner.nextLine();
				System.out.println("sold item       | sold date       | buyer ID   | price | commissions");
				System.out.println("--------------------------------------------------------------------");
				try { 
					PreparedStatement stmt = conn.prepareStatement("select item_id, sold_date, buyer_id, payment, payment*0.1 as commissions from billings natural join items where seller_id=?"); 
					stmt.setString(1, seller);
					ResultSet rset = stmt.executeQuery();
					while(rset.next()){
						System.out.println(String.format("%-17s %-17s %-12s %-7d %-12d", rset.getString("item_id"), rset.getDate("sold_date"), rset.getString("buyer_id"), rset.getInt("payment"), rset.getInt("commissions")));
					}
					stmt.close();
				}catch (SQLException e) {
					System.out.println(e);
					return false;
				}
				continue;
			} else if (choice == '3') {
				/*TODO: Print Seller Ranking */
				System.out.println("seller ID   | # of items sold | Total Profit (excluding commissions)");
				System.out.println("--------------------------------------------------------------------");
				try { 
					PreparedStatement stmt = conn.prepareStatement("select seller_id, count(item_id) as item_number, sum(payment) as total_profit, sum(floor(payment*0.1)) as total_commission from billings natural join items group by seller_id order by item_number desc, total_profit desc");
					ResultSet rset = stmt.executeQuery();
					while(rset.next()){
						System.out.println(String.format("%-13s %-17d %-37d", rset.getString("seller_id"), rset.getInt("item_number"), rset.getInt("total_profit")-rset.getInt("total_commission")));
					}
					stmt.close();
				}catch (SQLException e) {
					System.out.println(e);
					return false;
				}
				continue;
			} else if (choice == '4') {
				/*TODO: Print Buyer Ranking */
				System.out.println("buyer ID   | # of items purchased | Total Money Spent ");
				System.out.println("------------------------------------------------------");
				try { 
					PreparedStatement stmt = conn.prepareStatement("select buyer_id, count(item_id) as item_number, sum(payment) as total_spent from billings group by buyer_id order by item_number desc, total_spent desc");
					ResultSet rset = stmt.executeQuery();
					while(rset.next()){
						System.out.println(String.format("%-12s %-22d %-19d", rset.getString("buyer_id"), rset.getInt("item_number"), rset.getInt("total_spent")));
					}
					stmt.close();
				}catch (SQLException e) {
					System.out.println(e);
					return false;
				}
				continue;
			} else if (choice == 'P' || choice == 'p') {
				return false;
			} else {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}
		} while(true);
	}

	public static void CheckSellStatus(){
		/* TODO: Check the status of the item the current user is selling */

		System.out.println("item listed in Auction | bidder (buyer ID) | bidding price | bidding date/time ");
		System.out.println("-------------------------------------------------------------------------------");
		try { 
			PreparedStatement stmt = conn.prepareStatement("select item_id, bidder_id, sug_price, to_char(bidding_date, 'yyyy-mm-dd HH24:MI') as bidding_time from items natural left outer join bids where seller_id=? and status='AVAILABLE'");
			stmt.setString(1, username);
			ResultSet rset = stmt.executeQuery();
			while(rset.next()){
				String bidder_id = rset.getString("bidder_id");
				if(rset.wasNull()){
					System.out.println(String.format("%-24d %-19s %-15s %-19s", rset.getInt("item_id"), "NOT BIDDEN YET","-","-"));
				}else{
					System.out.println(String.format("%-24d %-19s %-15d %-19s", rset.getInt("item_id"), bidder_id, rset.getInt("sug_price"), rset.getString("bidding_time")));
				}
			}
			stmt.close();
		}catch (SQLException e) {
			System.out.println(e);
			return;
		}
	}

	public static boolean BuyItem(){
		Category category = Category.INVALID;
		Condition condition = Condition.INVALID;
		char choice;
		int item_choice;
		int price;
		String keyword, seller, datePosted;
		boolean flag_catg = true, flag_cond = true;
		LocalDateTime dateTime;

		do {

			System.out.println( "----< Select category > : \n" +
					"    1. Electronics\n"+
					"    2. Books\n" + 
					"    3. Home\n" + 
					"    4. Clothing\n" + 
					"    5. Sporting Goods\n" +
					"    6. Other categories\n" +
					"    7. Any category\n" +
					"    P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				return false;
			}

			flag_catg = true;

			switch (choice) {
				case '1':
					category = Category.ELECTRONICS;
					break;
				case '2':
					category = Category.BOOKS;
					break;
				case '3':
					category = Category.HOME;
					break;
				case '4':
					category = Category.CLOTHING;
					break;
				case '5':
					category = Category.SPORTINGGOODS;
					break;
				case '6':
					category = Category.OTHERS;
					break;
				case '7':
					break;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_catg = false;
					continue;
			}
		} while(!flag_catg);

		do {

			System.out.println(
					"----< Select the condition > \n" +
					"   1. New\n" +
					"   2. Like-new\n" +
					"   3. Used (Good)\n" +
					"   4. Used (Acceptable)\n" +
					"   P. Go Back to Previous Menu"
					);
			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				return false;
			}

			flag_cond = true;

			switch (choice) {
				case '1':
					condition = Condition.NEW;
					break;
				case '2':
					condition = Condition.LIKE_NEW;
					break;
				case '3':
					condition = Condition.GOOD;
					break;
				case '4':
					condition = Condition.ACCEPTABLE;
					break;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_cond = false;
					continue;
				}
		} while(!flag_cond);

		try {
			System.out.println("---- Enter keyword to search the description : ");
			keyword = scanner.next();
			scanner.nextLine();

			System.out.println("---- Enter Seller ID to search : ");
			System.out.println(" ** Enter 'any' if you want to see items from any seller. ");
			seller = scanner.next();
			scanner.nextLine();

			System.out.println("---- Enter date posted (YYYY-MM-DD): ");
			System.out.println(" ** This will search items that have been posted after the designated date.");
			datePosted = scanner.next();
			scanner.nextLine();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			dateTime = LocalDateTime.parse(datePosted+" 00:00", formatter);
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			return false;
		}

		/* TODO: Query condition: item category */
		/* TODO: Query condition: item condition */
		/* TODO: Query condition: items whose description match the keyword (use LIKE operator) */
		/* TODO: Query condition: items from a particular seller */
		/* TODO: Query condition: posted date of item */

		/* TODO: List all items that match the query condition */
		System.out.println("Item ID | Item description | Condition | Seller | Buy-It-Now | Current Bid | highest bidder | Time left    | bid close       ");
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
		boolean result_exist = false;
		ExpireItem();
		try { 
			PreparedStatement stmt = conn.prepareStatement("select item_id, description, condition, seller_id, bin_price, bidder_id, sug_price, to_char(closing_date-now(),'DD \"day\" HH24 \"hrs\"') as left_time, to_char(closing_date, 'yyyy-mm-dd HH24:MI') as closing_time from items natural left outer join bids where category like ? and condition=? and description like ? and posted_date>=? and seller_id LIKE ? and status='AVAILABLE' and closing_date > now() and is_highest is not false");
			stmt.setString(1, category==Category.INVALID ? "%" : category.name());
			stmt.setString(2, condition.name());
			stmt.setString(3, "%"+keyword+"%");
			stmt.setTimestamp(4, Timestamp.valueOf(dateTime));
			stmt.setString(5, seller.equalsIgnoreCase("any")?"%%":seller);
			ResultSet rset = stmt.executeQuery();
			while(rset.next()){
				result_exist = true;
				System.out.println(String.format("%-9s %-18s %-11s %-8s %-12d %-13d %-16s %-14s %-17s", rset.getString("item_id"), rset.getString("description"), rset.getString("condition"), rset.getString("seller_id"), 
					rset.getInt("bin_price"), rset.getInt("sug_price"), rset.getString("bidder_id"), rset.getString("left_time"), rset.getString("closing_time")));
			}
			stmt.close();
		}catch (SQLException e) {
			System.out.println(e);
			return false;
		}
		/* TODO: Buy-it-now or bid: If the entered price is higher or equal to Buy-It-Now price, the bid ends. */
		/* Even if the bid price is higher than the Buy-It-Now price, the buyer pays the B-I-N price. */
		if(result_exist){ 
			try { 
				System.out.println("---- Select Item ID to buy or bid: ");
				item_choice = scanner.nextInt();;
				scanner.nextLine();

				PreparedStatement stmt = conn.prepareStatement("select bin_price, sug_price from items natural left outer join bids where item_id=? and status='AVAILABLE' and is_highest is not false");
				stmt.setInt(1, item_choice);
				ResultSet rset = stmt.executeQuery();

				if(rset.next()){
					System.out.println("     Price: ");
					price = scanner.nextInt();
					scanner.nextLine();

					int bin_price = rset.getInt("bin_price");
					int curr_price = rset.getInt("sug_price");
					/* TODO: if you won, print the following */
					if(price >= bin_price){
						stmt = conn.prepareStatement("update bids set is_highest=false where item_id=? and is_highest=true");
						stmt.setInt(1, item_choice);
						stmt.executeUpdate();
						stmt = conn.prepareStatement("update items set status=? where item_id=?");
						stmt.setString(1, Status.SOLD.name());
						stmt.setInt(2, item_choice);
						stmt.executeUpdate();
						stmt = conn.prepareStatement("insert into bids values(?,?,?,true,now())");
						stmt.setInt(1, item_choice);
						stmt.setString(2, username);
						stmt.setInt(3, price);
						stmt.executeUpdate();
						stmt = conn.prepareStatement("insert into billings values(?, ?, ?, now())");
						stmt.setInt(1, item_choice);
						stmt.setString(2, username);
						stmt.setInt(3, bin_price);
						stmt.executeUpdate();
						System.out.println("Congratulations, the item is yours now.\n"); 
					}
					/* TODO: if you are the current highest bidder, print the following */
					else if(price > curr_price || rset.wasNull() ){			//caution using rset before wasNull
						stmt = conn.prepareStatement("update bids set is_highest=false where item_id=? and is_highest=true");
						stmt.setInt(1, item_choice);
						stmt.executeUpdate();
						stmt = conn.prepareStatement("insert into bids values(?,?,?,true,now())");
						stmt.setInt(1, item_choice);
						stmt.setString(2, username);
						stmt.setInt(3, price);
						stmt.executeUpdate();
						System.out.println("Congratulations, you are the highest bidder.\n"); 
					}
					else{
						System.out.println("You cannot bid at the price smaller than the current highest price.\n"); 
					}
				}else{
					stmt = conn.prepareStatement("select * from items where item_id=?");
					stmt.setInt(1, item_choice);
					rset = stmt.executeQuery();
					if(rset.next()){
						System.out.println("Bid Ended.");
					}else{
						System.out.println("No such Item.");
					}
				}
				stmt.close();
			}catch (SQLException e) {
				System.out.println(e);
				return false;
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				return false;
			}
			return true;
		} else {
			System.out.println("There is no corresponding result.");
			return true;
		}
	}

	public static void CheckBuyStatus(){
		/* TODO: Check the status of the item the current buyer is bidding on */
		/* Even if you are outbidded or the bid closing date has passed, all the items this user has bidded on must be displayed */

		System.out.println("item ID   | item description   | highest bidder | highest bidding price | your bidding price | bid closing date/time");
		System.out.println("--------------------------------------------------------------------------------------------------------------------");
		try { 
			PreparedStatement stmt = conn.prepareStatement("with highest(item_id, highest_bidder, highest_price) as (select item_id, bidder_id, sug_price from bids where is_highest=true) select item_id, description, highest_bidder, highest_price, sug_price, to_char(closing_date, 'yyyy-mm-dd HH24:MI') as closing_time from items natural join bids natural join highest where bidder_id=?");
			stmt.setString(1, username);
			ResultSet rset = stmt.executeQuery();
			while(rset.next()){
				System.out.println(String.format("%-11d %-20s %-16s %-23d %-20d %-22s", rset.getInt("item_id"), rset.getString("description"), rset.getString("highest_bidder"), rset.getInt("highest_price"), 
					rset.getInt("sug_price"), rset.getString("closing_time")));
			}
			stmt.close();
		}catch (SQLException e) {
			System.out.println(e);
			return;
		}
	}

	public static void CheckAccount(){
		/* TODO: Check the balance of the current user.  */
		System.out.println("[Sold Items]");
		System.out.println("item category  | item ID   | sold date | sold price  | buyer ID | commission  ");
		System.out.println("------------------------------------------------------------------------------");
		try { 
			PreparedStatement stmt = conn.prepareStatement("select item_id, category, sold_date, payment, buyer_id, payment*0.1 as commissions from billings natural join items where seller_id=?");
			stmt.setString(1, username);
			ResultSet rset = stmt.executeQuery();
			while(rset.next()){
				System.out.println(String.format("%-16s %-11d %-11s %-13d %-10s %-13d", rset.getString("category"), rset.getInt("item_id"), rset.getDate("sold_date"), rset.getInt("payment"), 
					rset.getString("buyer_id"), rset.getInt("commissions")));
			}
			stmt.close();
		}catch (SQLException e) {
			System.out.println(e);
			return;
		}
		System.out.println("[Purchased Items]");
		System.out.println("item category  | item ID   | purchased date | puchased price  | seller ID ");
		System.out.println("--------------------------------------------------------------------------");
		try { 
			PreparedStatement stmt = conn.prepareStatement("select item_id, category, sold_date, payment, seller_id from billings natural join items where buyer_id=?");
			stmt.setString(1, username);
			ResultSet rset = stmt.executeQuery();
			while(rset.next()){
				System.out.println(String.format("%-16s %-11d %-16s %-17d %-11s", rset.getString("category"), rset.getInt("item_id"), rset.getDate("sold_date"), rset.getInt("payment"), rset.getString("seller_id")));
			}
			stmt.close();
		}catch (SQLException e) {
			System.out.println(e);
			return;
		}
	}

	public static void main(String[] args) {
		char choice;
		boolean ret;

		if(args.length<2){
			System.out.println("Usage: java Auction postgres_id password");
			System.exit(1);
		}


		try{
			conn = DriverManager.getConnection("jdbc:postgresql://localhost/"+args[0], args[0], args[1]); 
            // conn = DriverManager.getConnection("jdbc:postgresql://localhost/2019312601", "2019312601", "0000"); 
		}
		catch(SQLException e){
			System.out.println("SQLException : " + e);	
			System.exit(1);
		}

		do {
			username = null;
			System.out.println(
					"----< Login menu >\n" + 
					"----(1) Login\n" +
					"----(2) Sign up\n" +
					"----(3) Login as Administrator\n" +
					"----(Q) Quit"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			ExpireItem();
			try {
				switch ((int) choice) {
					case '1':
						ret = LoginMenu();
						if(!ret) continue;
						break;
					case '2':
						ret = SignupMenu();
						if(!ret) continue;
						break;
					case '3':
						ret = AdminMenu();
						if(!ret) continue;
					case 'q':
					case 'Q':
						System.out.println("Good Bye");
						/* TODO: close the connection and clean up everything here */
						conn.close();
						System.exit(0);
					default:
						System.out.println("Error: Invalid input is entered. Try again.");
				}
			} catch (SQLException e) {
				System.out.println("SQLException : " + e);	
			}
		} while (username==null || username.equalsIgnoreCase("back"));  

		// logged in as a normal user 
		do {
			System.out.println(
					"---< Main menu > :\n" +
					"----(1) Sell Item\n" +
					"----(2) Status of Your Item Listed on Auction\n" +
					"----(3) Buy Item\n" +
					"----(4) Check Status of your Bid \n" +
					"----(5) Check your Account \n" +
					"----(Q) Quit"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			ExpireItem();
			try{
				switch (choice) {
					case '1':
						ret = SellMenu();
						if(!ret) continue;
						break;
					case '2':
						CheckSellStatus();
						break;
					case '3':
						ret = BuyItem();
						if(!ret) continue;
						break;
					case '4':
						CheckBuyStatus();
						break;
					case '5':
						CheckAccount();
						break;
					case 'q':
					case 'Q':
						System.out.println("Good Bye");
						/* TODO: close the connection and clean up everything here */
						conn.close();
						System.exit(0);
				}
			} catch (SQLException e) {
				System.out.println("SQLException : " + e);	
				System.exit(1);
			}
		} while(true);
	} // End of main 
} // End of class


