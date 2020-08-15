package com.example.animalwelfare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title;
    private Button DogButton;
    private Button CatButton;

    private String abc;

    ViewPager pager;
    MyPageAdapter pageAdapter;
    MyPageAdapter2 pageAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        title =(TextView) findViewById(R.id.info_titletextView);
        DogButton = (Button) findViewById(R.id.info_dogbutton);
        CatButton = (Button) findViewById(R.id.info_catbutton);
        pager = (ViewPager) findViewById(R.id.info_viewpager);


        DogButton.setOnClickListener(this);

        CatButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.info_dogbutton:
                List<Fragment> fragments = getFragments();
                pageAdapter = new MyPageAdapter(getSupportFragmentManager(),fragments);
                pager.setAdapter(pageAdapter);
                break;

            case R.id.info_catbutton:
                List<Fragment> fragments2 = getFragments2();
                pageAdapter2 = new MyPageAdapter2(getSupportFragmentManager(),fragments2);
                pager.setAdapter(pageAdapter2);
                break;
        }
    }


    private List<Fragment> getFragments()
    {
        List<Fragment> fragmentslist = new ArrayList<>();

         fragmentslist.add(MyFragment.newInstance("German Shepherd","Weiqht:-\n" + "Male30-40 kg (66-88 lb) \n" + "Female22-33 kg (49-73 lb) \n" +
                 "Heiqht :-\n" + "Male60-65 cm (24-26 in) \n" +
                 "Female55-60 cm (22-24 in) \n" +
                 "Colour:-\n" + "Most commonly tan with black saddlery \n" +
                 "Life span:- \n" + "9 - 13 yea rs \n" +
                 "Description:- \n" +
                 "The German Shepherd is one of the most widely used breeds in a wide variety of scent-work roles. These include search and rescue, cadaver searching, narcotics detection, explosives detection, accelerant detection and mine detection dog, among others. ", R.drawable.dog1 ));

         fragmentslist.add(MyFragment.newInstance("BullDog", "Origin:- England \n" +
                 "Weight:- \n" + "Male 50-551b (23-25 kg) \n" + "Female 40-501b (18-23 kg) \n" +
                 "Height:- \n" + "Male l -2 feet \n" + "Female l-2 feet \n" +
                 "Color:- \n" + "Brindle, solid white, red, fawn or yellow, piebald. \n" +
                 "Life span: \n" + "8- 12 years \n" +
                 "Description:- \n" +
                 "The Bulldog, also known as the British Bulldog or English Bulldog, is a medium-sized breed of dog. It is a muscular, hefty dog with a wrinkled face and a distinctive pushed-in nose. Bulldogs are popular pets, they were the fourth most popular purebreed in the US in 2016 according to the American Kennel Club ", R.drawable.dog2 ));

         fragmentslist.add(MyFragment.newInstance("Labrador Retriever", "Origin:- United Kingdom and Canada \n" +
                 "Weight:- \n" + "Male 65-80 lb (29-36 kg) \n" + "Female 55-701b (25-32 kg) \n" +
                 "Colour:- \n" + "Black, chocolate, or yellow (ranges from pale yellow (nearly white) to fox red) \n" +
                 "Life span:- \n" + "12- 13 years \n" +
                 "Description: \n" +
                 "A favourite disability assistance breed in many countries, Labradors are frequently trained to aid the blind, those who have autism, to act as a therapy dog, or to perform screening and detection work for law enforcement and other official agencies.Additionally, they are prized as sporting and hunting dogs.",R.drawable.dog3 ));

         return fragmentslist;
    }

    private List<Fragment> getFragments2()
    {
        List<Fragment> fragmentslist2 = new ArrayList<>();

        fragmentslist2.add(MyFragment.newInstance("Persian Cat", "Origin :- lran \n" +
                "Weight :- \n" + "Male 5.5 kg \n" + "Female 3.5 to 5.5 kg \n" +
                "Height:- \n" + "10 to 15 inches \n" +
                "Color :- \n" + "Black and White \n" +
                "Life span :- \n" + "12 to 15 years \n" +
                "Description:- \n" +
                "The Persian cat is a long-haired breed of cat characterized by its round face and short muzzle. It is also known as the \"Persian Longhair\" in the English-speaking countries. In the Middle East, region they are widely known as \"Iranian cat\" and in Iran they are known as \"Shirazi cat\". " ,R.drawable.cat1 ));

        fragmentslist2.add(MyFragment.newInstance("Russian Blue", "Origin:- Russia \n" +
                "Weight:-\n" + "4.5kg Male and Female \n" +
                "Height:-\n" + "60 cm \n" +
                "Color :-\n" + "Slate grey and dark \n" +
                "Life span:-\n" + "15 to 20 years \n" +
                "Description:-\n" +
                "The Russian Blue is a cat breed that comes in colors varying from a light shimmering silver to a darker, slate grey. They develop close bonds with their owners and are sought out as pets due to their personalities, beauty and coat. ",R.drawable.cat2 ));

        fragmentslist2.add(MyFragment.newInstance("Maine Coon", "Origin:- Maine. United states \n" +
                "Weiqht:-\n" + "Male 5.9 - 8.2 kq (Adult), \n" + "Female 3.6 - 5.4 ka (Adult) \n" +
                "Height:-\n" + "50 -55 cm \n" + "Color:-\n" + "Brownish \n" +
                "Life span:- \n" + "Average 10 to 12 years. \n" +
                "Description:-\n" +
                "The Maine Coon is the largest domesticated cat breed. It has a distinctive physical appearance and valuable hunting skills. It is one of the oldest natural breeds in North America, specifically native to the state of Maine, where it is the official state cat. ",R.drawable.cat3 ));

        return fragmentslist2;
    }


    private class MyPageAdapter extends FragmentPagerAdapter
    {
        private List<Fragment> fragments;
        private int[] mResources;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments )
        {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public int getCount()
        {
            return this.fragments.size();
        }

        @Override
        public Fragment getItem(int position)
        {
            return this.fragments.get(position);
        }
    }


    private class MyPageAdapter2 extends FragmentPagerAdapter
    {
        private List<Fragment> fragments2;
        private int[] mResources;

        public MyPageAdapter2(FragmentManager fm, List<Fragment> fragments2 )
        {
            super(fm);
            this.fragments2 = fragments2;
        }

        @Override
        public int getCount()
        {
            return this.fragments2.size();
        }

        @Override
        public Fragment getItem(int position)
        {
            return this.fragments2.get(position);
        }
    }


}
