package me.panavtec.drawableview.utils;

import android.graphics.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import me.panavtec.drawableview.draw.SerializablePath;

/**
 * Created by linjingsheng on 17/4/11.
 */

public class SerializeUtils {


    private SerializeUtils() {
        throw new AssertionError();
    }

    /**
     * Deserialization object from file.
     *
     * @param filePath file path
     * @return de-serialized object
     * @throws RuntimeException if an error occurs
     */
    public static Object deserialization(String filePath) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(filePath));
            Object o = in.readObject();
            in.close();
            return o;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ClassNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.close(in);
        }
    }

    /**
     * Serialize object to file.
     *
     * @param filePath file path
     * @param obj object
     * @throws RuntimeException if an error occurs
     */
    public static void serialization(String filePath, Object obj) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filePath));
            out.writeObject(obj);
            out.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.close(out);
        }
    }



    public  static ArrayList<SerializablePath>  TransPath(ArrayList<SerializablePath> pvbaths){
        ArrayList<SerializablePath> list=new ArrayList<SerializablePath>();

        for (int j=0;j<pvbaths.size();j++){
            SerializablePath serializablePath=new SerializablePath();
            SerializablePath oned=pvbaths.get(j);
            for (int i=0;i<oned.getPathPoints().size();i++){

//                if (pvbaths.get(j).getColor()==Color.WHITE){
//                    System.out.print(""+pvbaths.get(j).getColor());
//                }
                serializablePath.addPathPoints(oned.getPathPoints().get(i));
                serializablePath.setColor(pvbaths.get(j).getColor());

                serializablePath.loadPathPointsAsQuadTo();

            }
            serializablePath.setWidth(oned.getWidth());
            list.add(serializablePath);
        }

        return  list;
    }

}
