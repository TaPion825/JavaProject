package com.example.administrator.game;

import com.example.administrator.framework.AppManager;
import com.example.administrator.framework.Graphic;
import com.example.administrator.framework.ObjectManager;
import com.example.administrator.framework.R;

/**
 * Created by Administrator on 2017-12-15.
 */

public class HP_Yellow extends Graphic {
    private int width = 368, height = 44;
    HP_Black hp_black;
    double HP_Ratio_Ori = 1;
    double HP_Ratio_Pre = 1;
    double HP_Ratio_Past = 1;
    int check_Timer;

    public HP_Yellow(HP_Black hp_black) {
        super(AppManager.getInstance().getBitmap(R.drawable.hp_yellow));
        this.hp_black = hp_black;
        this.InitSpriteData(height, width, 1.5f);
        setPosition(hp_black.getX() + 25, 48);
    }

    //TODO 노란색 HP바를 천천히 줄어들게함
    public void Update(long GameTime) {
        if(check_Timer > 20) {
            if (HP_Ratio_Past - HP_Ratio_Pre >= 0.1) {
                ObjectManager.hp_Black_Vector.get(ObjectManager.hp_Yellow_Vector.indexOf(this)).setShake(GameTime);
                check_Timer = 0;
            }
            HP_Ratio_Past = HP_Ratio_Pre;
        }

        check_Timer++;

        HP_Ratio_Past = HP_Ratio_Pre;

        if(HP_Ratio_Ori > HP_Ratio_Pre) {
            HP_Ratio_Ori -= 0.005;
            this.InitSpriteData(height, (int)(width*HP_Ratio_Ori), 1.5f);
        } else {
            HP_Ratio_Ori = HP_Ratio_Pre;
        }
    }

    public void setHP_Ratio(double HP_Ratio) {
        this.HP_Ratio_Pre = HP_Ratio;
    }
}
