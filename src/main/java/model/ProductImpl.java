package model;

import props.Product;
import utils.DB;

import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductImpl implements IProduct{
    DB db=new DB();
    List<Product> ls = new ArrayList<>(); // 1
    List<Product> lsSearch = new ArrayList<>();// 2

    public ProductImpl() {
        ls = productList(); //veritabanına baglan yeni veri cek
        lsSearch = ls; // yeni verileri  lsSearch  ye ekle
    }

    @Override
    public int productInsert(Product product) {
        ls=lsSearch;
        int status=0;
        try{
            String sql="INSERT INTO Product values(null,?,?,?,?,?,?)";
            PreparedStatement pre = db.connect().prepareStatement(sql);
            pre.setString(1,product.getName());
            pre.setInt(2,product.getCategoryId());
            pre.setInt(3,product.getBuyPrice());
            pre.setInt(4,product.getSellPrice());
            pre.setString(5,product.getInfo());
            pre.setInt(6,product.getStock());
            status = pre.executeUpdate();
        }catch (Exception e){
            System.out.println("productInsert Error : "+e);
        }
        finally {
            db.close();
        }
        return status;
    }

    @Override
    public int productUpdate(Product product) {
        ls=lsSearch;
        int status = 0;
        try{
            String sql=" UPDATE product SET name= ?,categoryId = ?,buyPrice = ?, bellPrice =?, info =?, stock =?  where pid=?";
            PreparedStatement pre = db.connect().prepareStatement(sql);
            pre.setString(1,product.getName());
            pre.setInt(2,product.getCategoryId());
            pre.setInt(3,product.getBuyPrice());
            pre.setInt(4,product.getSellPrice());
            pre.setString(5,product.getInfo());
            pre.setInt(6,product.getStock());
            pre.setInt(7,product.getPid());
            status = pre.executeUpdate();
        }catch (Exception e){
            System.out.println("productUpdate Error : "+e);
        }
        finally {
            db.close();
        }
        return status;
    }

    @Override
    public int productDelete(int pid) {
        int status=0;
        try{
            String sql="DELETE FROM product WHERE pid = ?";
            PreparedStatement pre = db.connect().prepareStatement(sql);
            pre.setInt(1,pid);
            status = pre.executeUpdate();
        }catch (Exception e){
            System.out.println("productDelete Error : "+e);
        }
        finally {
            db.close();
        }
        return status;
    }

    @Override
    public List<Product> productList() {
        List<Product> productList = new ArrayList<>();
        try
        {
            String sql = "select * from product order by pid desc";
            PreparedStatement pre=db.connect().prepareStatement(sql);
            ResultSet rs=pre.executeQuery();
            while(rs.next())
            {
                int pid=rs.getInt("pid");
                String pName = rs.getString("name");
                int pCategoryId = rs.getInt("categoryId");
                int pBuyPrice = rs.getInt("buyPrice");
                int pSellPrice = rs.getInt("sellPrice");
                String pInfo = rs.getString("info");
                int pStock = rs.getInt("stock");
                Product product = new Product(pid,pName,pCategoryId,pBuyPrice,pSellPrice,pInfo,pStock);
                productList.add(product);
            }
        }
        catch (Exception ex)
        {
            System.err.println("productList Error: "+ex.toString());
            ex.printStackTrace();
        }
        finally {
            db.close();
        }
        return productList;
    }

    @Override
    public DefaultTableModel productTable(String data) {
        ls=lsSearch;
        //en başta bir column isimleri oluşturulması gerekiyor.
        DefaultTableModel md = new DefaultTableModel();
        md.addColumn("id");
        md.addColumn("Name");
        md.addColumn("CategoryId");
        md.addColumn("BuyPrice");
        md.addColumn("SellPrice");
        md.addColumn("Info");
        md.addColumn("Stock");

        //1.ilkönce ne istendiğine gore konum al
        if (data != null && !data.equals("")) {//arama sonuclarını gonder
            List<Product> subLs = new ArrayList<>();
            for (Product item : ls) {
                if (item.getName().toLowerCase(Locale.ROOT).contains(data)
                        || item.getInfo().toLowerCase(Locale.ROOT).contains(data)) {
                    subLs.add(item);
                }
            }
            ls = subLs;
        }

        for (Product item : ls) {
            Object[] row = {item.getPid(), item.getName(), item.getCategoryId(),
                    item.getBuyPrice(), item.getSellPrice(), item.getInfo(), item.getStock()};
            md.addRow(row);
        }
        return md;
    }
    }

