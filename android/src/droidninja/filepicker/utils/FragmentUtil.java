package droidninja.filepicker.utils;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import droidninja.filepicker.fragments.BaseFragment;
import ti.filepicker.Utils;

public class FragmentUtil
{
  public FragmentUtil() {}

  public static boolean hadFragment(AppCompatActivity activity)
  {
    return activity.getSupportFragmentManager().getBackStackEntryCount() != 0;
  }

  public static void replaceFragment(AppCompatActivity activity, int contentId, BaseFragment fragment) {
    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

    transaction.setCustomAnimations(Utils.getR("anim.slide_left_in"), Utils.getR("anim.slide_left_out"));

    transaction.replace(contentId, fragment, fragment.getClass().getSimpleName());

    transaction.addToBackStack(null);
    transaction.commit();
  }

  public static void addFragment(AppCompatActivity activity, int contentId, BaseFragment fragment)
  {
    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

    transaction.setCustomAnimations(Utils.getR("anim.slide_left_in"), Utils.getR("anim.slide_left_out"));

    transaction.add(contentId, fragment, fragment.getClass().getSimpleName());

    transaction.commit();
  }


  public static void removeFragment(AppCompatActivity activity, BaseFragment fragment)
  {
    activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
  }



  public static void showFragment(AppCompatActivity activity, BaseFragment fragment)
  {
    activity.getSupportFragmentManager().beginTransaction().show(fragment).commit();
  }


  public static void hideFragment(AppCompatActivity activity, BaseFragment fragment)
  {
    activity.getSupportFragmentManager().beginTransaction().hide(fragment).commit();
  }


  public static void attachFragment(AppCompatActivity activity, BaseFragment fragment)
  {
    activity.getSupportFragmentManager().beginTransaction().attach(fragment).commit();
  }


  public static void detachFragment(AppCompatActivity activity, BaseFragment fragment)
  {
    activity.getSupportFragmentManager().beginTransaction().detach(fragment).commit();
  }

  public static android.support.v4.app.Fragment getFragmentByTag(AppCompatActivity appCompatActivity, String tag)
  {
    return appCompatActivity.getSupportFragmentManager().findFragmentByTag(tag);
  }
}
