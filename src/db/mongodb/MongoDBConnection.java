package db.mongodb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.sun.org.apache.bcel.internal.generic.NEW;

import static com.mongodb.client.model.Filters.eq;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;


public class MongoDBConnection implements DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase db;
	
	public MongoDBConnection() {
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}
	
	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (db == null) {
			return;
		}
		db.getCollection("users").updateOne(new Document("user_id", userId), 
				new Document("$push", new Document("favorite", new Document("$each", itemIds))));
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (db == null) {
			return;
		}
		db.getCollection("users").updateOne(new Document("user_id", userId), 
				new Document("$pullAll", new Document("favorite", itemIds)));
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (db == null) {
			return new HashSet<>();
		}
		Set<String> favoriteItemSet = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		if (iterable.first() != null && iterable.first().containsKey("favorite")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("favorite");
			favoriteItemSet.addAll(list);
		}
		
		return favoriteItemSet;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (db == null) {
			return new HashSet<>();
		}
		Set<Item> favoriteItemSet = new HashSet<>();
		
		Set<String> itemIdSet = getFavoriteItemIds(userId);
		for (String itemId : itemIdSet) {
			FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id",itemId));
			if (iterable.first() != null) {
				Document doc = iterable.first();
				
				ItemBuilder builder = new ItemBuilder();
				builder.setItemId(doc.getString("item_id"));
				builder.setName(doc.getString("name"));
				builder.setAddress(doc.getString("Address"));
				builder.setUrl(doc.getString("url"));
				builder.setImageUrl(doc.getString("image_url"));
				builder.setRating(doc.getDouble("rating"));
				builder.setDistance(doc.getDouble("distance"));
				builder.setCategories(getCategories(itemId));
				
				favoriteItemSet.add(builder.build());
			} 
		}
		
		return favoriteItemSet;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		if (db == null) {
			return new HashSet<>();
		}
		Set<String> categoriesSet = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id",itemId));
		
		if (iterable.first() != null && iterable.first().containsKey("categories")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("categories");
			categoriesSet.addAll(list);
		}
		return categoriesSet;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, term);
		for (Item item : items) {
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
		if (db == null) {
			return;
		}
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", item.getItemId()));
		if (iterable.first() != null) {
			return;
		}
		
		if (iterable.first() == null) {
			db.getCollection("items").insertOne(new Document()
					.append("item_id", item.getItemId())
					.append("distance", item.getDistance())
					.append("name", item.getName())
					.append("address", item.getAddress())
					.append("url", item.getUrl())
					.append("image_url", item.getImageUrl())
					.append("rating", item.getRating())
					.append("categories", item.getCategories()));
		}
	}

	@Override
	public String getFullname(String userId) {
		FindIterable<Document> iterable =
				db.getCollection("users").find(new Document("user_id", userId));
		Document document = iterable.first();
		String firstName = document.getString("first_name");
		String lastName = document.getString("last_name");
		return firstName + " " + lastName;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		FindIterable<Document> iterable =
		        db.getCollection("users").find(new Document("user_id", userId));
		Document document = iterable.first();
		return document.getString("password").equals(password);
	}

	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
