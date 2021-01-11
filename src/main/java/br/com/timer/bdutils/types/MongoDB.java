package br.com.timer.bdutils.types;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import br.com.timer.bdutils.models.Params;
import br.com.timer.bdutils.models.ResultSetContents;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MongoDB extends ConnectionBase {

	// Collection = Table

	private final String URI, database, collectionName;
	private MongoDatabase mongoDatabase;

	@Override
	public void openConnection() {
		MongoClientURI clientURI = new MongoClientURI(System.getProperty(URI));
		try (MongoClient client = new MongoClient(clientURI)) {
			this.client = client;
			this.mongoDatabase = client.getDatabase(this.database);
		}
	}

	@Override
	public void closeConnection() {
		client.close();
	}

	@Override
	public void insert(List<Params> insertValues) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		Document document = new Document();
		insertValues.forEach(value -> document.append(value.getKey(), value.getValue()));
		collection.insertOne(document);
	}

	@Override
	public void delete(Params found) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		collection.findOneAndDelete(new Document(found.getKey(), found.getValue()));
	}

	@Override
	public void delete(List<Params> found) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		Document document = new Document();
		found.forEach(value -> document.append(value.getKey(), value.getValue()));
		collection.findOneAndDelete(document);
	}

	// Found -> UUID = UUID.KingZ_
	@Override
	public void update(Params found, List<Params> updateValues) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		Optional.of(collection.find(new Document(found.getKey(), found.getValue())).first())
				.ifPresent(consumer -> updateValues.forEach(where -> {
					Bson valueUpdate = new Document(where.getKey(), where.getValue());
					Bson command = new Document("$set", valueUpdate);
					collection.updateOne(valueUpdate, command);
				}));
	}

	@Override
	public Optional<ResultSetContents> select(List<Params> paramsList) {
		// TODO Auto-generated method stub
		return null;
	}

}
