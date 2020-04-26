package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

import android.app.Application;
import android.content.Context;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeConfiguration;
import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.generation.MazeContainer;

public class StaticData{

    private static MazeConfiguration mc = null;

    private StaticData(){};

    public static MazeConfiguration getMC(){
        if(mc==null){
            mc=new MazeContainer();
        }
        return mc;
    }

    public static void setMC(MazeConfiguration mazeConfiguration){
        mc = mazeConfiguration;
    }
}