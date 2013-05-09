package com.atlantbh.collections.lucene;

import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

import com.atlantbh.utils.serialization.Serializer;

public class LuceneAdapter<T> {
	public static final FieldType LOOKUP_FIELD_TYPE = new FieldType() {{
		// indexed as keyword
		setIndexed(true);
		setTokenized(false);
		setOmitNorms(true);
		setIndexOptions(IndexOptions.DOCS_ONLY);
		// not stored
		setStored(false);
	}};
	
	public static final FieldType RETRIEVABLE_LOOKUP_FIELD_TYPE = new FieldType(LOOKUP_FIELD_TYPE) {{
		setStored(true);
	}};
	

	private static final String BASE64_SUFFIX = "64";
	
	private final String objFieldName;
	private FieldType objFieldType;
	
	public LuceneAdapter(String objFieldName) {
		this(objFieldName, RETRIEVABLE_LOOKUP_FIELD_TYPE);
	}
	
	public LuceneAdapter(String objFieldName, FieldType objFieldType) {
		this.objFieldName = objFieldName;
		this.objFieldType = objFieldType;
	}
	
	public Document toDocument(T obj) {
		Document doc = new Document();
		
		// add entire object to document
		addField(doc, objFieldName, obj, objFieldType);
		
		// check if empty document
		if (doc.getFields().isEmpty()) {
			throw new UnsupportedOperationException("Document empty");
		}
		return doc;
	}

	protected void addField(Document doc, String fieldName, Object obj, FieldType type) {
		if (obj instanceof String) {
			doc.add(new Field(fieldName, (String)obj, type));
		} else if (obj instanceof Serializable) {	
			byte[] objBytes = Serializer.toByteArray(obj);
			// indexed and stored must go into separate fields - one for searching and other for retrieving
			if (type.stored()) {
				doc.add(new BinaryDocValuesField(fieldName, new BytesRef(objBytes)));
			}
			if (type.indexed()) {
				doc.add(new Field(fieldName + BASE64_SUFFIX, Base64.encodeBase64String(objBytes), LOOKUP_FIELD_TYPE));
			}
		} else {
			throw new UnsupportedOperationException("Object must implement Serializable.");
		}
	}
	
	public Query getUniquenessQuery(T obj) {
		return getQuery(objFieldName, obj);
	}

	public Query getQuery(String fieldName, Object value) {
		return new TermQuery(getTerm(fieldName, value));
	}
	
	public Term getTerm(String fieldName, Object value) {
		if (value instanceof String) {
			return new Term(fieldName, (String)value);
		} else if (value instanceof Serializable) {
			return new Term(fieldName + BASE64_SUFFIX, Base64.encodeBase64String(Serializer.toByteArray(value)));
		} else {
			throw new UnsupportedOperationException("Object must implement Serializable.");
		}
	}
	
	protected Object getFieldValue(Document doc, String fieldName) {
		IndexableField field = doc.getField(fieldName);
		if (field != null) {
			if (field.fieldType().stored()) {
				BytesRef binary = field.binaryValue();
				if (binary != null) {
					return Serializer.fromByteArray(binary.bytes, binary.offset, binary.length);
				} else {
					return field.stringValue();
				}
			} else {
				throw new UnsupportedOperationException("Fields must be stored in order to be rocoverable from index");
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public T toObject(Document doc) {
		return (T) getFieldValue(doc, objFieldName);
	}
}
