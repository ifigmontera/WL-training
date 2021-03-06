package com.alan.slidingmenu.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.slidingmenu.BDD.ExoBDD;
import com.alan.slidingmenu.BDD.ExoDico;
import com.alan.slidingmenu.Classe.ExpandableListAdapter;
import com.alan.slidingmenu.Classe.User;
import com.alan.slidingmenu.Classe.UserPreferences;
import com.alan.slidingmenu.Popup.PopupDescExercice;
import com.alan.slidingmenu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DicoExerciceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NomHeader {
    private NavigationView navigationView;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    private ExoDico parceExo;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<ExoDico> exos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dico_exercice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        UserPreferences pref = new UserPreferences(this);
        User user = pref.readUser();

        setNomHeader(user);

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                /*Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();*/
                Intent intent = new Intent(DicoExerciceActivity.this, PopupDescExercice.class);
                Bundle b = new Bundle();
                for (ExoDico e : exos) {
                    if (e.getCategorie().equals(listDataHeader.get(groupPosition))) {
                        if (e.getStringExo().equals(listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition))) {
                            Log.e("test", listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition));
                            intent.putExtra("exo", e);
                        }

                    }
                }


                startActivity(intent);
                return false;
            }
        });
        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        exos = new ArrayList<ExoDico>();
        String[] categorie;

        ExoBDD bd = new ExoBDD(getApplicationContext());
        bd.open();
        exos = bd.getExo();
        listDataHeader = bd.getCategorie();
        bd.close();


        List[] cat = new List[listDataHeader.size()];
        initialisation(cat);
        remplissage(cat, exos);


        for (int i = 0; i < listDataHeader.size(); i++) {
            listDataChild.put(listDataHeader.get(i), cat[i]); // Header, Child data
        }


    }

    private void remplissage(List[] cat, List<ExoDico> exos) {
        for (ExoDico e : exos) {
            for (int i = 0; i < listDataHeader.size(); i++) {
                if (e.getCategorie().equals(listDataHeader.get(i))) {
                    cat[i].add(e.getStringExo());
                }
            }

        }
    }

    private void initialisation(List[] cat) {
        for (int i = 0; i < cat.length; i++) {

            cat[i] = new ArrayList<String>();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        com.alan.slidingmenu.Activity.Menu.goTo(this, id);
        finish();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setNomHeader(User user) {
        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUserName);
        if (user != User.USER_VIDE)
            name.setText(user.getPseudo() + " (" + user.getEmail() + ")");
        else {
            name.setText("Non connecté");
        }
    }
}
