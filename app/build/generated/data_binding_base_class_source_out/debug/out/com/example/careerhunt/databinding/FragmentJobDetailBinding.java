// Generated by view binder compiler. Do not edit!
package com.example.careerhunt.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.careerhunt.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentJobDetailBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView QuestionAns;

  @NonNull
  public final Button btnApply;

  @NonNull
  public final ImageButton btnBack;

  @NonNull
  public final Button btnCheckLocation;

  @NonNull
  public final ImageButton btnSave;

  @NonNull
  public final RecyclerView carouselRecyclerView;

  @NonNull
  public final ImageView comIcon;

  @NonNull
  public final TextView comName;

  @NonNull
  public final TextView companyAddressResl;

  @NonNull
  public final TextView companyPhoneResl;

  @NonNull
  public final TextView descResult;

  @NonNull
  public final TextView descTitle;

  @NonNull
  public final ConstraintLayout detailBottom;

  @NonNull
  public final ConstraintLayout detailContainer;

  @NonNull
  public final ConstraintLayout detailMiddle;

  @NonNull
  public final ConstraintLayout detailTop;

  @NonNull
  public final TextView expectedSalary;

  @NonNull
  public final TextView jobCat;

  @NonNull
  public final TextView jobLoca;

  @NonNull
  public final TextView jobTi;

  @NonNull
  public final TextView jobTy;

  @NonNull
  public final TextView lblCompanyAddress;

  @NonNull
  public final TextView lblSalaryResult;

  @NonNull
  public final TextView lblpostDate;

  @NonNull
  public final View line;

  @NonNull
  public final View line2;

  @NonNull
  public final TextView lvlPostDateResult;

  @NonNull
  public final ConstraintLayout noPhtLayout;

  @NonNull
  public final ScrollView scrollView2;

  @NonNull
  public final TextView textView13;

  @NonNull
  public final TextView textView14;

  @NonNull
  public final ConstraintLayout uploadResume;

  private FragmentJobDetailBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView QuestionAns, @NonNull Button btnApply, @NonNull ImageButton btnBack,
      @NonNull Button btnCheckLocation, @NonNull ImageButton btnSave,
      @NonNull RecyclerView carouselRecyclerView, @NonNull ImageView comIcon,
      @NonNull TextView comName, @NonNull TextView companyAddressResl,
      @NonNull TextView companyPhoneResl, @NonNull TextView descResult, @NonNull TextView descTitle,
      @NonNull ConstraintLayout detailBottom, @NonNull ConstraintLayout detailContainer,
      @NonNull ConstraintLayout detailMiddle, @NonNull ConstraintLayout detailTop,
      @NonNull TextView expectedSalary, @NonNull TextView jobCat, @NonNull TextView jobLoca,
      @NonNull TextView jobTi, @NonNull TextView jobTy, @NonNull TextView lblCompanyAddress,
      @NonNull TextView lblSalaryResult, @NonNull TextView lblpostDate, @NonNull View line,
      @NonNull View line2, @NonNull TextView lvlPostDateResult,
      @NonNull ConstraintLayout noPhtLayout, @NonNull ScrollView scrollView2,
      @NonNull TextView textView13, @NonNull TextView textView14,
      @NonNull ConstraintLayout uploadResume) {
    this.rootView = rootView;
    this.QuestionAns = QuestionAns;
    this.btnApply = btnApply;
    this.btnBack = btnBack;
    this.btnCheckLocation = btnCheckLocation;
    this.btnSave = btnSave;
    this.carouselRecyclerView = carouselRecyclerView;
    this.comIcon = comIcon;
    this.comName = comName;
    this.companyAddressResl = companyAddressResl;
    this.companyPhoneResl = companyPhoneResl;
    this.descResult = descResult;
    this.descTitle = descTitle;
    this.detailBottom = detailBottom;
    this.detailContainer = detailContainer;
    this.detailMiddle = detailMiddle;
    this.detailTop = detailTop;
    this.expectedSalary = expectedSalary;
    this.jobCat = jobCat;
    this.jobLoca = jobLoca;
    this.jobTi = jobTi;
    this.jobTy = jobTy;
    this.lblCompanyAddress = lblCompanyAddress;
    this.lblSalaryResult = lblSalaryResult;
    this.lblpostDate = lblpostDate;
    this.line = line;
    this.line2 = line2;
    this.lvlPostDateResult = lvlPostDateResult;
    this.noPhtLayout = noPhtLayout;
    this.scrollView2 = scrollView2;
    this.textView13 = textView13;
    this.textView14 = textView14;
    this.uploadResume = uploadResume;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentJobDetailBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentJobDetailBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_job_detail, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentJobDetailBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.QuestionAns;
      TextView QuestionAns = ViewBindings.findChildViewById(rootView, id);
      if (QuestionAns == null) {
        break missingId;
      }

      id = R.id.btnApply;
      Button btnApply = ViewBindings.findChildViewById(rootView, id);
      if (btnApply == null) {
        break missingId;
      }

      id = R.id.btnBack;
      ImageButton btnBack = ViewBindings.findChildViewById(rootView, id);
      if (btnBack == null) {
        break missingId;
      }

      id = R.id.btnCheckLocation;
      Button btnCheckLocation = ViewBindings.findChildViewById(rootView, id);
      if (btnCheckLocation == null) {
        break missingId;
      }

      id = R.id.btnSave;
      ImageButton btnSave = ViewBindings.findChildViewById(rootView, id);
      if (btnSave == null) {
        break missingId;
      }

      id = R.id.carousel_recycler_view;
      RecyclerView carouselRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (carouselRecyclerView == null) {
        break missingId;
      }

      id = R.id.comIcon;
      ImageView comIcon = ViewBindings.findChildViewById(rootView, id);
      if (comIcon == null) {
        break missingId;
      }

      id = R.id.comName;
      TextView comName = ViewBindings.findChildViewById(rootView, id);
      if (comName == null) {
        break missingId;
      }

      id = R.id.companyAddressResl;
      TextView companyAddressResl = ViewBindings.findChildViewById(rootView, id);
      if (companyAddressResl == null) {
        break missingId;
      }

      id = R.id.companyPhoneResl;
      TextView companyPhoneResl = ViewBindings.findChildViewById(rootView, id);
      if (companyPhoneResl == null) {
        break missingId;
      }

      id = R.id.descResult;
      TextView descResult = ViewBindings.findChildViewById(rootView, id);
      if (descResult == null) {
        break missingId;
      }

      id = R.id.descTitle;
      TextView descTitle = ViewBindings.findChildViewById(rootView, id);
      if (descTitle == null) {
        break missingId;
      }

      id = R.id.detailBottom;
      ConstraintLayout detailBottom = ViewBindings.findChildViewById(rootView, id);
      if (detailBottom == null) {
        break missingId;
      }

      ConstraintLayout detailContainer = (ConstraintLayout) rootView;

      id = R.id.detailMiddle;
      ConstraintLayout detailMiddle = ViewBindings.findChildViewById(rootView, id);
      if (detailMiddle == null) {
        break missingId;
      }

      id = R.id.detailTop;
      ConstraintLayout detailTop = ViewBindings.findChildViewById(rootView, id);
      if (detailTop == null) {
        break missingId;
      }

      id = R.id.expectedSalary;
      TextView expectedSalary = ViewBindings.findChildViewById(rootView, id);
      if (expectedSalary == null) {
        break missingId;
      }

      id = R.id.jobCat;
      TextView jobCat = ViewBindings.findChildViewById(rootView, id);
      if (jobCat == null) {
        break missingId;
      }

      id = R.id.jobLoca;
      TextView jobLoca = ViewBindings.findChildViewById(rootView, id);
      if (jobLoca == null) {
        break missingId;
      }

      id = R.id.jobTi;
      TextView jobTi = ViewBindings.findChildViewById(rootView, id);
      if (jobTi == null) {
        break missingId;
      }

      id = R.id.jobTy;
      TextView jobTy = ViewBindings.findChildViewById(rootView, id);
      if (jobTy == null) {
        break missingId;
      }

      id = R.id.lblCompanyAddress;
      TextView lblCompanyAddress = ViewBindings.findChildViewById(rootView, id);
      if (lblCompanyAddress == null) {
        break missingId;
      }

      id = R.id.lblSalaryResult;
      TextView lblSalaryResult = ViewBindings.findChildViewById(rootView, id);
      if (lblSalaryResult == null) {
        break missingId;
      }

      id = R.id.lblpostDate;
      TextView lblpostDate = ViewBindings.findChildViewById(rootView, id);
      if (lblpostDate == null) {
        break missingId;
      }

      id = R.id.line;
      View line = ViewBindings.findChildViewById(rootView, id);
      if (line == null) {
        break missingId;
      }

      id = R.id.line2;
      View line2 = ViewBindings.findChildViewById(rootView, id);
      if (line2 == null) {
        break missingId;
      }

      id = R.id.lvlPostDateResult;
      TextView lvlPostDateResult = ViewBindings.findChildViewById(rootView, id);
      if (lvlPostDateResult == null) {
        break missingId;
      }

      id = R.id.noPhtLayout;
      ConstraintLayout noPhtLayout = ViewBindings.findChildViewById(rootView, id);
      if (noPhtLayout == null) {
        break missingId;
      }

      id = R.id.scrollView2;
      ScrollView scrollView2 = ViewBindings.findChildViewById(rootView, id);
      if (scrollView2 == null) {
        break missingId;
      }

      id = R.id.textView13;
      TextView textView13 = ViewBindings.findChildViewById(rootView, id);
      if (textView13 == null) {
        break missingId;
      }

      id = R.id.textView14;
      TextView textView14 = ViewBindings.findChildViewById(rootView, id);
      if (textView14 == null) {
        break missingId;
      }

      id = R.id.uploadResume;
      ConstraintLayout uploadResume = ViewBindings.findChildViewById(rootView, id);
      if (uploadResume == null) {
        break missingId;
      }

      return new FragmentJobDetailBinding((ConstraintLayout) rootView, QuestionAns, btnApply,
          btnBack, btnCheckLocation, btnSave, carouselRecyclerView, comIcon, comName,
          companyAddressResl, companyPhoneResl, descResult, descTitle, detailBottom,
          detailContainer, detailMiddle, detailTop, expectedSalary, jobCat, jobLoca, jobTi, jobTy,
          lblCompanyAddress, lblSalaryResult, lblpostDate, line, line2, lvlPostDateResult,
          noPhtLayout, scrollView2, textView13, textView14, uploadResume);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
