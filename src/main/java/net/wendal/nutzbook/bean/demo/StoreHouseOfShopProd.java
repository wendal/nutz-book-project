package net.wendal.nutzbook.bean.demo;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

/**
 * 仓库表
 */
@PK({ "storehouseId", "shopProdId" })
@Table("storehouse_shop_prod")

public class StoreHouseOfShopProd {

	@Column("shop_product_id")
	private long shopProdId;

	@Column("storehouse_id")
	private int storehouseId;

	@Column("stock")
	private Integer stock;

	@Column("remarks")
	private String remarks;

	public long getShopProdId() {
		return shopProdId;
	}

	public void setShopProdId(long shopProdId) {
		this.shopProdId = shopProdId;
	}

	public int getStorehouseId() {
		return storehouseId;
	}

	public void setStorehouseId(int storehouseId) {
		this.storehouseId = storehouseId;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}