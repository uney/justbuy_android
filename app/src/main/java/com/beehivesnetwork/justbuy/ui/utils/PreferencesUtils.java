package com.beehivesnetwork.justbuy.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.LinkedHashSet;
import java.util.Set;

public class PreferencesUtils {
    private SharedPreferences sharedPreferences;
    private final static String PREF_VERSION = "1";
    public PreferencesUtils(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences("md_setting", Context.MODE_PRIVATE);
    }

    public void resetAllPreferences() {
        sharedPreferences.edit().clear().commit();
    }

    public String getStringForKey(String key, String defValue) {
        return sharedPreferences.getString(key+PREF_VERSION, defValue);
    }

    public void setStringForKey(String key, String value) {
        Editor editor = sharedPreferences.edit();
        editor.putString(key+PREF_VERSION, value);
        editor.commit();
    }

    public Set<String> getStringSetForKey(String key, Set<String> defValue) {
        return sharedPreferences.getStringSet(key+PREF_VERSION, defValue);
    }

    public void setStringSetForKey(String key, Set<String> value) {
        Editor editor = sharedPreferences.edit();
        editor.putStringSet(key+PREF_VERSION, value);
        editor.commit();
    }

    public boolean isExist(String key){
        return sharedPreferences.contains(key+PREF_VERSION);
    }


    public int getIntForKey(String key, int defValue) {
        return sharedPreferences.getInt(key+PREF_VERSION, defValue);
    }

    public void setIntForKey(String key, int value) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(key+PREF_VERSION, value);
        editor.commit();
    }

    public long getLongForKey(String key, long defValue) {
        return sharedPreferences.getLong(key+PREF_VERSION, defValue);
    }

    public void setLongForKey(String key, long value) {
        Editor editor = sharedPreferences.edit();
        editor.putLong(key+PREF_VERSION, value);
        editor.commit();
    }

    public void  setDoubleForKey(final String key, final double value) {
        Editor editor = sharedPreferences.edit();
        editor.putLong(key+PREF_VERSION, Double.doubleToRawLongBits(value));
        editor.commit();
    }

    public double getDoubleForKey(final String key, final double defaultValue) {
        return Double.longBitsToDouble(sharedPreferences.getLong(key + PREF_VERSION, Double.doubleToLongBits(defaultValue)));
    }

    public boolean getBoolForKey(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key+PREF_VERSION, defValue);
    }

    public void setBoolForKey(String key, boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key+PREF_VERSION, value);
        editor.commit();
    }

    public float getFloatForKey(String key, float defValue) {
        return sharedPreferences.getFloat(key+PREF_VERSION, defValue);
    }

    public void setFloatForKey(String key, float value) {
        Editor editor = sharedPreferences.edit();
        editor.putFloat(key+PREF_VERSION, value);
        editor.commit();
    }

    public void removeKey(String key) {
        Editor editor = sharedPreferences.edit();
        editor.remove(key+PREF_VERSION);
        editor.commit();
    }

    public void setSerializableArrayForKey(String key, Serializable[] serializableObject) {
        Editor editor = sharedPreferences.edit();
        Set<String> objStringArray = new LinkedHashSet<>();
        try {
            for (Serializable s:serializableObject) {
                objStringArray.add(getStringFromSerializable(s));
            }
            editor.putStringSet(key+PREF_VERSION,objStringArray).commit();
        } catch (IOException | NullPointerException e) {
            Log.e("API", "Can not set serializable from object: " + e.getMessage());
        }
    }

    public <T> T[] getSerializableArrayForKey(String key,T[] serializableArray) throws InvalidClassException {
        Set<String> strObjSet = sharedPreferences.getStringSet(key+PREF_VERSION, null);
        if(strObjSet == null)
            return null;

        if(serializableArray instanceof Serializable) {
            int size = strObjSet.size();
            if (strObjSet == null || strObjSet.size() == 0) {
                Log.e("API", "Can not find object set the key or it is empty: " + key);
                return null;
            }

            serializableArray = (T[]) Array.newInstance(serializableArray.getClass().getComponentType(), size);

            try {
                int index = 0;
                for (String s : strObjSet) {
                    serializableArray[index++] = (T) getSerializableFromString(s);
                }
                return serializableArray;
            } catch (IOException | ClassNotFoundException e) {
                Log.e("API", "Can not get serializable array from share preference: " + e.getMessage() + ", key: " + key + " removed.");
                removeKey(key+PREF_VERSION);
                return null;
            }
        } else {
            throw new InvalidClassException("Can not get seriablizable object from non-seriablizable param");
        }
    }


    public void setSerializableForKey(String key, Serializable serializableObject) {
        Editor editor = sharedPreferences.edit();
        try {
            String objString = getStringFromSerializable(serializableObject);
            editor.putString(key+PREF_VERSION, objString).commit();
            Log.d("API", "serializable object saved to share preference: " + objString);
        } catch (IOException e) {
            Log.e("API", "Can not write serializable to share preference: " + e.getMessage());
        }
    }

    public Serializable getSerializableForKey(String key) {
        String strObj = sharedPreferences.getString(key+PREF_VERSION, null);
        if (strObj == null) {
            Log.e("API", "Can not find the key: " + key);
            return null;
        }

        try {
            Serializable obj = getSerializableFromString(strObj);
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            Log.e("API", "Can not get serializable from share preference: " + e.getMessage());
            return null;
        }
    }


    private String getStringFromSerializable(Serializable serializableObject) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objOs = new ObjectOutputStream(bos);
        objOs.writeObject(serializableObject);
        return Base64.encode(bos.toByteArray(), Base64.DEFAULT).toString();
    }

    private Serializable getSerializableFromString(String objectInString) throws IOException, ClassNotFoundException {
        byte[] base64Byte = Base64.decode(objectInString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Byte);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (Serializable) ois.readObject();
    }
}
