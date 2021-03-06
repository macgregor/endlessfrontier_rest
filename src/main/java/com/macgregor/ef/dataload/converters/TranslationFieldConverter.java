package com.macgregor.ef.dataload.converters;

import com.macgregor.ef.dataload.annotations.Translate;
import com.macgregor.ef.exceptions.CanonicalConversionException;
import com.macgregor.ef.model.canonical.Translation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationFieldConverter {
    private static final Logger logger = LoggerFactory.getLogger(TranslationFieldConverter.class);
    private SessionFactory sessionFactory;


    public TranslationFieldConverter(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public Object convert(Object obj, Field f) throws CanonicalConversionException {
        logger.debug(String.format("[%s %010d] - beginning translation of field %s", obj.getClass().getSimpleName(), System.identityHashCode(obj), f.getName()));
        String processedKey = "";
        try {
            processedKey = getFieldKey(obj, f);
            Translation translation = findTranslation(processedKey);
            if(!StringUtils.isBlank(translation.getValue())) {
                logger.debug(String.format("[%s %010d] - Successful translation", obj.getClass().getSimpleName(), System.identityHashCode(obj)));
                return translation.getValue();
            } else {
                logger.warn(String.format("[%s %010d] - Translation for %s found but blank, not overriding", obj.getClass().getSimpleName(), System.identityHashCode(obj), processedKey));
            }
        } catch (CanonicalConversionException e){
            logger.warn(String.format("[%s %010d] - Translation Error: %s", obj.getClass().getSimpleName(), System.identityHashCode(obj), e.getMessage()));
        } catch (NullPointerException e){
            logger.warn(String.format("[%s %010d] - Translation Error: key %s not found in database", obj.getClass().getSimpleName(), System.identityHashCode(obj), processedKey));
        }

        try{
          return FieldUtils.readField(f, obj, true).toString();
        } catch (IllegalAccessException e){
            throw new CanonicalConversionException(String.format("[%s %010d] - Failed to do just about everything with field %s", obj.getClass().getSimpleName(), System.identityHashCode(obj), f.getName()), e);
        } catch (NullPointerException e){
            return null;
        }
    }

    public String getFieldKey(Object obj, Field f) throws CanonicalConversionException {
        Translate translateAnnotation = f.getAnnotation(Translate.class);
        String translationKey = translateAnnotation.key();

        logger.debug(String.format("[%s %010d] - Processing translation key for field %s - raw key %s", obj.getClass().getSimpleName(), System.identityHashCode(obj), f.getName(), translationKey));

        String processedKey = getFieldKey(obj, translationKey);

        logger.debug(String.format("[%s %010d] - Processing translation key for field %s - processed key %s", obj.getClass().getSimpleName(), System.identityHashCode(obj), f.getName(), processedKey));

        return processedKey;
    }

    public String getFieldKey(Object obj, String key) throws CanonicalConversionException {
        String processedKey = key;

        Pattern fieldPattern = Pattern.compile(".*?(\\{(.*?)\\}).*?");
        Matcher fieldMatcher = fieldPattern.matcher(key);
        while (fieldMatcher.find()) {
            String fieldReference = fieldMatcher.group(2);
            Field f = FieldUtils.getField(obj.getClass(), fieldReference, true);
            if(f == null){
                throw new CanonicalConversionException(String.format("[%s %010d] - Invalid field reference in @Translate annotation: raw key: %s, unknown field reference: %s", obj.getClass().getSimpleName(), System.identityHashCode(obj), key, fieldReference));
            }

            try {
                String lookup = FieldUtils.readField(f, obj, true).toString();
                processedKey = processedKey.replace(fieldMatcher.group(1), lookup);
            } catch (IllegalAccessException e) {
                throw new CanonicalConversionException(String.format("[%s %010d] - Unexpected error reading field %s, this probably shouldnt ever happen", obj.getClass().getSimpleName(), System.identityHashCode(obj), fieldReference), e);
            } catch (NullPointerException e) {
                throw new CanonicalConversionException(String.format("[%s %010d] - Error reading field %s, value is probably null", obj.getClass().getSimpleName(), System.identityHashCode(obj), fieldReference), e);
            }

        }
        return processedKey;
    }

    public Translation findTranslation(String id){
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Translation t = session.get(Translation.class, id);
        tx.commit();
        session.close();
        return t;
    }
}
