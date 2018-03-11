package com.macgregor.ef.dataload.converters;

import com.macgregor.ef.dataload.annotations.Translate;
import com.macgregor.ef.exceptions.CanonicalConversionException;
import com.macgregor.ef.exceptions.DataLoadException;
import com.macgregor.ef.model.canonical.Translation;
import com.macgregor.ef.util.MockTranslationFieldConverter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class TranslationFieldConverterTest {

    private static final Logger logger = LoggerFactory.getLogger(TranslationFieldConverterTest.class);

    private FieldTranslationConverterTestModel testModel;
    private TranslationFieldConverter translationFieldConverter;

    public static class FieldTranslationConverterTestModel implements Serializable {
        public Integer id;

        public Integer id2;

        public Object none;

        private Integer privateId;

        @Translate(key="KEY_{id}")
        public String keyWithFieldReference;

        @Translate(key="KEY_{id}_{id2}")
        public String keyWithMultipleFieldReference;

        @Translate(key="KEY")
        public String simpleKey;

        @Translate(key="KEY_{doesntexist}")
        public String invalidFieldReference;

        @Translate(key="KEY_{id}_{id}")
        public String repeatedFieldReference;

        @Translate(key="KEY_{none}")
        public String nullFieldReference;

        @Translate(key="KEY_{privateId}")
        private String privateFieldReference;

        public Integer getPrivateId(){ return this.privateId; }

        public void setPrivateId(Integer privateId){ this.privateId = privateId; }

        public void setPrivateFieldReference(String privateFieldReference){
            this.privateFieldReference = privateFieldReference;
        }

        public String getPrivateFieldReference(){
            return privateFieldReference;
        }
    }

    @Before
    public void setup(){
        translationFieldConverter = new MockTranslationFieldConverter();
        testModel = new FieldTranslationConverterTestModel();
        testModel.id = 1;
        testModel.id2 = 2;
        testModel.keyWithFieldReference = "untranslated";
        testModel.keyWithMultipleFieldReference = "untranslated";
        testModel.simpleKey = "untranslated";
        testModel.invalidFieldReference = "untranslated";
        testModel.repeatedFieldReference = "untranslated";
        testModel.setPrivateFieldReference("untranslated");
        testModel.setPrivateId(3);
    }

    @Test
    public void testGetFieldKeyExtractsSimpleKey() throws DataLoadException{
        String extracted = translationFieldConverter.getFieldKey(testModel, "KEY");
        assertEquals("KEY", extracted);
    }

    @Test
    public void testGetFieldKeyExtractsKeyWithFieldReference() throws DataLoadException{
        String extracted = translationFieldConverter.getFieldKey(testModel, "KEY_{id}");
        assertEquals("KEY_1", extracted);
    }

    @Test
    public void testGetFieldKeyExtractsKeyWithMultipleFieldReferences() throws DataLoadException {
        String extracted = translationFieldConverter.getFieldKey(testModel, "KEY_{id}_{id2}");
        assertEquals("KEY_1_2", extracted);
    }

    @Test
    public void testGetFieldKeyExtractsKeyWithRepeatedFieldReferences() throws DataLoadException {
        String extracted = translationFieldConverter.getFieldKey(testModel, "KEY_{id}_{id}");
        assertEquals("KEY_1_1", extracted);
    }

    @Test
    public void testGetFieldKeyExtractsKeysFromPrivateFields() throws DataLoadException {
        String extracted = translationFieldConverter.getFieldKey(testModel, "KEY_{privateId}");
        assertEquals("KEY_3", extracted);
    }

    @Test(expected = CanonicalConversionException.class)
    public void testGetFieldKeyThrowsDataLoadExceptionWithInvalidKey() throws DataLoadException {
        String extracted = translationFieldConverter.getFieldKey(testModel, "KEY_{doesntexist}");
    }

    @Test(expected = CanonicalConversionException.class)
    public void testGetFieldKeyThrowsDataLoadExceptionWithNullKey() throws DataLoadException {
        String extracted = translationFieldConverter.getFieldKey(testModel, "KEY_{none}");
    }

    @Test
    public void testTranslateDoesntThrowNPEWhenTranslationEntityNotFound() throws CanonicalConversionException {
        ((MockTranslationFieldConverter) translationFieldConverter).setTranslation(null);
        Field f = FieldUtils.getField(testModel.getClass(), "keyWithFieldReference");
        translationFieldConverter.convert(testModel, f);
    }

    @Test
    public void testTranslateDoesntOverrideWithBlankTranslationValue() throws CanonicalConversionException {
        Translation t = new Translation();
        t.setId("1");
        t.setValue("");
        ((MockTranslationFieldConverter) translationFieldConverter).setTranslation(t);
        Field f = FieldUtils.getField(testModel.getClass(), "simpleKey");
        String translated = (String)translationFieldConverter.convert(testModel, f);
        assertEquals("untranslated", translated);
    }

    @Test
    public void testTraslateDoesntThrowNullPointerExceptionWhenTranslationLookupFails() throws CanonicalConversionException {
        ((MockTranslationFieldConverter) translationFieldConverter).setTranslation(null);
        Field f = FieldUtils.getField(testModel.getClass(), "simpleKey");
        String translated = (String)translationFieldConverter.convert(testModel, f);
        assertEquals("untranslated", translated);
    }

    @Test
    public void testTraslateDoesntThrowNullPointerExceptionWhenTranslationLookupFailsAndSourceFieldIsNull() throws CanonicalConversionException {
        ((MockTranslationFieldConverter) translationFieldConverter).setTranslation(null);
        testModel.simpleKey = null;
        Field f = FieldUtils.getField(testModel.getClass(), "simpleKey");
        String translated = (String)translationFieldConverter.convert(testModel, f);
        assertEquals(null, translated);
    }
}
