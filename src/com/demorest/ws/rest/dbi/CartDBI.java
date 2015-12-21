package com.demorest.ws.rest.dbi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import com.demorest.ws.rest.manager.LogManager;
import com.demorest.ws.rest.connect.DataBaseManager;
import com.demorest.ws.rest.util.UtilsFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CartDBI {
	
	/**
	 * To validate the given user is valid or not 
	 * @param strUserName
	 * @param strPass
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public JSONObject userAuthentication(String strUserName, String strPass, Connection con) throws Exception {
		
		StringBuilder sbQry = new StringBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONObject joAuth = new JSONObject();;
		
		try {
			
			joAuth.put("validuser", false);
			
			sbQry.append("select customerid,firstname,lastname,email from usermaster where email=? and password=? ");
			
			pstmt = con.prepareStatement(sbQry.toString());
			pstmt.setString(1, strUserName);
			pstmt.setString(2, strPass);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				joAuth.put("validuser", true);
				
				joAuth.put("customer_id", rs.getLong("customerid"));
				joAuth.put("first_name", rs.getString("firstname"));
				joAuth.put("last_name", rs.getString("lastname"));
				joAuth.put("email_id", rs.getString("email"));
				
				
			}
		} catch(Exception ex) {			
			LogManager.errorLog(ex);
		} finally {
			
			UtilsFactory.clearCollectionHieracy(sbQry);
		}
		
		return joAuth;
			
	}
	/**
	 * To get the history of all purchase items
	 * @param lUserId
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPurchaseHistory(long lUserId, Connection con) throws Exception {
		
		StringBuilder sbQry = new StringBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray jaPurchaseItems = new JSONArray();
		JSONObject joitem = null;
		JSONObject joReturn = null;
		
		try {
			if(lUserId==-1) {
				sbQry.append("select productname, productdescription, o.orderdate, o.ordernumber from orders o inner join  orderdetails od on o.orderid = od.orderid ")
				.append("inner join products p on p.productid = od.productid  and o.paid=true inner join usermaster um on um.customerid=o.customerid ");
			}else {
				sbQry.append("select productname, productdescription, o.orderdate, o.ordernumber from orders o inner join  orderdetails od on o.orderid = od.orderid ")
				.append("inner join products p on p.productid = od.productid  and o.paid=true inner join usermaster um on um.customerid=o.customerid and o.customerid ='"+lUserId+"' ");
			}
			
			pstmt = con.prepareStatement(sbQry.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				joitem = new JSONObject();
				
				joitem.put("product_name", rs.getString("productname"));
				joitem.put("product_desc", rs.getString("productdescription"));
				joitem.put("order_date", rs.getString("orderdate"));
				joitem.put("order_number", rs.getString("ordernumber"));			
				
				jaPurchaseItems.add(joitem);
			}
		} catch(Exception ex) {			
			LogManager.errorLog(ex);
		}  finally {
			
			DataBaseManager.close(pstmt);
			pstmt = null;
			
			DataBaseManager.close(rs);
			rs = null;
			
			UtilsFactory.clearCollectionHieracy(sbQry);
			
			joReturn = new JSONObject();
			joReturn.put("PurchaseItems", jaPurchaseItems);
			
		}
		
		
		
		return joReturn;
			
	}
	
	/**
	 * To get the order details by providing the customer ID as 
	 * input to this function 
	 * @param lUserId
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public JSONArray getOrderDetails(long lUserId, Connection con) throws Exception {
		
		StringBuilder sbQry = new StringBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray jaOrderDetails = new JSONArray();
		JSONObject joOrder = null;
		
		try {
			sbQry.append("select * from orders o  inner join orderdetails od on o.orderid=od.orderid and o.customerid=? and o.paid=false ")
					.append("inner join products p on  p.productid = od.productid");
			
			pstmt = con.prepareStatement(sbQry.toString());
			pstmt.setLong(1, lUserId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				joOrder = new JSONObject();
				joOrder.put("order_id", rs.getInt("orderid"));
				joOrder.put("product_id", rs.getInt("productid"));
				joOrder.put("product_name", rs.getString("productname"));
				joOrder.put("product_desc", rs.getString("productdescription"));
				joOrder.put("unit_price", rs.getFloat("unitprice"));
				joOrder.put("quantity", rs.getInt("quantity"));
				
				jaOrderDetails.add(joOrder);
			}
		} catch(Exception ex) {			
			LogManager.errorLog(ex);
		}  finally {
			
			DataBaseManager.close(pstmt);
			pstmt = null;
			
			DataBaseManager.close(rs);
			rs = null;
			
			UtilsFactory.clearCollectionHieracy(sbQry);
		}
		
		return jaOrderDetails;
		
		
		
	}
	
	/**
	 * To add the selected products to the cart
	 * @param con
	 * @param lUserId
	 * @param lProductId
	 * @param nQuantity
	 * @return
	 * @throws Exception
	 */
	public JSONObject addProductToCart(Connection con, long lUserId, long lProductId, int nQuantity) throws Exception {
		StringBuilder sbQry = new StringBuilder();
		PreparedStatement pstmt = null;
		PreparedStatement pstmtOD = null, pstmtRN = null;
		ResultSet rs = null;
		JSONObject joRtn = null;
		long lOrderNumber = -1l;
		
		long lOrderid = -1;
		try {
			lOrderNumber = (int)Math.floor(Math.random() * (999999 - 1));
			sbQry.append("select ordernumber from orders WHERE paid = false and customerid = ? order by orderid desc LIMIT 1");
			pstmtRN = con.prepareStatement(sbQry.toString());
			pstmtRN.setLong(1, lUserId);
			rs = pstmtRN.executeQuery();
			if(rs.next()){
				lOrderNumber = rs.getLong("ordernumber");
			}else{
				lOrderNumber = (int)Math.floor(Math.random() * (999999 - 1));
			}
			
			sbQry.setLength(0);
			sbQry.append("insert into orders(customerid,ordernumber, shipperid) values(")
				.append(lUserId).append(",")
//				.append(" (SELECT case when EXISTS(SELECT ordernumber FROM orders WHERE paid = false and customerid = 1 order by orderid desc LIMIT 1) is true then ordernumber else '")
//				.append(lOrderNumber)
//				.append("' end FROM orders WHERE paid = false and customerid = 1 order by orderid desc ) ")
				.append(lOrderNumber)
				.append(",")
				.append("(select shipperid from shippers")
				.append("))");
			pstmt = con.prepareStatement(sbQry.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.executeUpdate();
			
			lOrderid = DataBaseManager.returnKey(pstmt);
			
			sbQry.setLength(0);
			
			sbQry.append("insert into orderdetails(orderid,productid, ordernumber, price, quantity) values(")
			.append(lOrderid).append(", ")	
			.append(lProductId).append(", ")
				.append(lOrderNumber).append(", ")
				.append("(select unitprice from products where productid = ").append(lProductId).append(")").append(", ")
				.append(nQuantity).append(")");
			
			pstmtOD = con.prepareStatement(sbQry.toString());
			pstmtOD.executeUpdate();
			joRtn = new JSONObject();
			joRtn.put("success", "Your item has been added to cart successfully");
		} catch (Exception e) {
			// TODO: handle exception
		}  finally {
			
			DataBaseManager.close(pstmt);
			pstmt = null;
			
			UtilsFactory.clearCollectionHieracy(sbQry);
		}
		
		return joRtn;
	}
	
	/**
	 * To purchase the product in cart 
	 * @param con
	 * @param lUserId
	 * @return
	 * @throws Exception
	 */
	public JSONObject productPurchase(Connection con, long lUserId) throws Exception {
		StringBuilder sbQry = new StringBuilder();
		PreparedStatement pstmt = null;
		PreparedStatement pstmtOD = null, pstmtadd = null;
		ResultSet rs = null;
		ResultSet rsAddress = null;
		JSONArray jaOrders = new JSONArray();
		JSONObject joOrder = null, joReturn = new JSONObject();
		long lOrderId = -1l;
		
		try {
			sbQry.append("update orders set paid=true, orderdate = now(), paymentdate = now(), shipdate  = now(), paymentid = ? where customerid = ? and paid = false");
			pstmt = con.prepareStatement(sbQry.toString());
			
			pstmt.setInt(1, (int)Math.floor(Math.random() * (999999 - 1)));
			pstmt.setLong(2, lUserId);
			pstmt.executeUpdate();

			sbQry.setLength(0);
			
			sbQry.append("select productname, unitprice, o.ordernumber, od.productid, od.quantity from products p inner join  orderdetails od on p.productid = od.productid ")
					.append("inner join orders o on o.orderid = od.orderid and o.ordernumber = (select ordernumber from orders where customerid = ? order by orderdate desc limit 1)");
			pstmtOD = con.prepareStatement(sbQry.toString());
			pstmtOD.setLong(1, lUserId);
			
			rs = pstmtOD.executeQuery();
			while(rs.next()){
				joOrder = new JSONObject();
				joOrder.put("productname", rs.getString("productname"));
				joOrder.put("price", rs.getFloat("unitprice"));
				lOrderId = rs.getLong("ordernumber");
				jaOrders.add(joOrder);
				
				decreaseQuantity(con, rs.getLong("productid"), rs.getInt("quantity"));
			}
			
			
			sbQry.setLength(0);
			joReturn.put("invoice_id", lOrderId);
			joReturn.put("products", jaOrders);
			
			sbQry.append("select (billingaddress ||','|| billingcity ||','|| billingregion ||','|| billingpostalcode) as billingaddress , (shipaddress ||','|| shipcity ||','|| shipregion ||','|| shippostalcode) as shipaddress from usermaster where customerid=? ");
			pstmtadd = con.prepareStatement(sbQry.toString());
			pstmtadd.setLong(1, lUserId);
			rsAddress = pstmtadd.executeQuery();
				
			while(rsAddress.next()){
				joReturn.put("billing_address", rsAddress.getString("billingaddress"));
				joReturn.put("shipping_address", rsAddress.getString("shipaddress"));			
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}  finally {
			
			DataBaseManager.close(pstmt);
			pstmt = null;
			
			DataBaseManager.close(pstmtadd);
			pstmtadd = null; 
			
			DataBaseManager.close(pstmtOD);
			pstmtOD = null;
			
			DataBaseManager.close(rs);
			rs = null;
			
			DataBaseManager.close(rsAddress);
			rsAddress = null;
			
			UtilsFactory.clearCollectionHieracy(sbQry);
		}
		
		return joReturn;
	}
	/**
	 * To reduce the quantity
	 * @param con
	 * @param lProductId
	 * @param nQuantity
	 * @throws Exception
	 */
	public void decreaseQuantity(Connection con, long lProductId, int nQuantity) throws Exception {
		StringBuilder sbQry = new StringBuilder();
		PreparedStatement pstmt = null;
		try {
			sbQry.append("update products set unitsinstock = unitsinstock - ")
				.append(nQuantity)
				.append(" where productid = ?");
			pstmt = con.prepareStatement(sbQry.toString());
			pstmt.setLong(1, lProductId);
			pstmt.executeQuery();
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			DataBaseManager.close(pstmt);
			pstmt = null;
		}
	}
}
