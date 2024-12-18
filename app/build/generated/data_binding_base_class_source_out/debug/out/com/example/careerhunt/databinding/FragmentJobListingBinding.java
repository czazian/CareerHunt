// Generated by view binder compiler. Do not edit!
package com.example.careerhunt.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.careerhunt.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentJobListingBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageButton btnAddJob;

  @NonNull
  public final Button btnShowAllLatest;

  @NonNull
  public final Button btnShowAllRecommended;

  @NonNull
  public final ConstraintLayout container;

  @NonNull
  public final TextView lblLatestTitle;

  @NonNull
  public final TextView lblUserName;

  @NonNull
  public final TextView lblWelcome;

  @NonNull
  public final NestedScrollView nestedScrollView;

  @NonNull
  public final RecyclerView newJobRecyclerView;

  @NonNull
  public final ConstraintLayout newPostedContainer;

  @NonNull
  public final TextView noJobIndicator;

  @NonNull
  public final ConstraintLayout overallContainer;

  @NonNull
  public final ShapeableImageView profileImage;

  @NonNull
  public final CircularProgressIndicator progressIndicator;

  @NonNull
  public final ConstraintLayout recommendedContainer;

  @NonNull
  public final RecyclerView recommendedJobRecyclerView;

  @NonNull
  public final ConstraintLayout topContainer;

  @NonNull
  public final TextView txtRecommendedTitle;

  private FragmentJobListingBinding(@NonNull ConstraintLayout rootView,
      @NonNull ImageButton btnAddJob, @NonNull Button btnShowAllLatest,
      @NonNull Button btnShowAllRecommended, @NonNull ConstraintLayout container,
      @NonNull TextView lblLatestTitle, @NonNull TextView lblUserName, @NonNull TextView lblWelcome,
      @NonNull NestedScrollView nestedScrollView, @NonNull RecyclerView newJobRecyclerView,
      @NonNull ConstraintLayout newPostedContainer, @NonNull TextView noJobIndicator,
      @NonNull ConstraintLayout overallContainer, @NonNull ShapeableImageView profileImage,
      @NonNull CircularProgressIndicator progressIndicator,
      @NonNull ConstraintLayout recommendedContainer,
      @NonNull RecyclerView recommendedJobRecyclerView, @NonNull ConstraintLayout topContainer,
      @NonNull TextView txtRecommendedTitle) {
    this.rootView = rootView;
    this.btnAddJob = btnAddJob;
    this.btnShowAllLatest = btnShowAllLatest;
    this.btnShowAllRecommended = btnShowAllRecommended;
    this.container = container;
    this.lblLatestTitle = lblLatestTitle;
    this.lblUserName = lblUserName;
    this.lblWelcome = lblWelcome;
    this.nestedScrollView = nestedScrollView;
    this.newJobRecyclerView = newJobRecyclerView;
    this.newPostedContainer = newPostedContainer;
    this.noJobIndicator = noJobIndicator;
    this.overallContainer = overallContainer;
    this.profileImage = profileImage;
    this.progressIndicator = progressIndicator;
    this.recommendedContainer = recommendedContainer;
    this.recommendedJobRecyclerView = recommendedJobRecyclerView;
    this.topContainer = topContainer;
    this.txtRecommendedTitle = txtRecommendedTitle;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentJobListingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentJobListingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_job_listing, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentJobListingBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnAddJob;
      ImageButton btnAddJob = ViewBindings.findChildViewById(rootView, id);
      if (btnAddJob == null) {
        break missingId;
      }

      id = R.id.btnShowAllLatest;
      Button btnShowAllLatest = ViewBindings.findChildViewById(rootView, id);
      if (btnShowAllLatest == null) {
        break missingId;
      }

      id = R.id.btnShowAllRecommended;
      Button btnShowAllRecommended = ViewBindings.findChildViewById(rootView, id);
      if (btnShowAllRecommended == null) {
        break missingId;
      }

      ConstraintLayout container = (ConstraintLayout) rootView;

      id = R.id.lblLatestTitle;
      TextView lblLatestTitle = ViewBindings.findChildViewById(rootView, id);
      if (lblLatestTitle == null) {
        break missingId;
      }

      id = R.id.lblUserName;
      TextView lblUserName = ViewBindings.findChildViewById(rootView, id);
      if (lblUserName == null) {
        break missingId;
      }

      id = R.id.lblWelcome;
      TextView lblWelcome = ViewBindings.findChildViewById(rootView, id);
      if (lblWelcome == null) {
        break missingId;
      }

      id = R.id.nestedScrollView;
      NestedScrollView nestedScrollView = ViewBindings.findChildViewById(rootView, id);
      if (nestedScrollView == null) {
        break missingId;
      }

      id = R.id.newJobRecyclerView;
      RecyclerView newJobRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (newJobRecyclerView == null) {
        break missingId;
      }

      id = R.id.newPostedContainer;
      ConstraintLayout newPostedContainer = ViewBindings.findChildViewById(rootView, id);
      if (newPostedContainer == null) {
        break missingId;
      }

      id = R.id.noJobIndicator;
      TextView noJobIndicator = ViewBindings.findChildViewById(rootView, id);
      if (noJobIndicator == null) {
        break missingId;
      }

      id = R.id.overallContainer;
      ConstraintLayout overallContainer = ViewBindings.findChildViewById(rootView, id);
      if (overallContainer == null) {
        break missingId;
      }

      id = R.id.profileImage;
      ShapeableImageView profileImage = ViewBindings.findChildViewById(rootView, id);
      if (profileImage == null) {
        break missingId;
      }

      id = R.id.progressIndicator;
      CircularProgressIndicator progressIndicator = ViewBindings.findChildViewById(rootView, id);
      if (progressIndicator == null) {
        break missingId;
      }

      id = R.id.recommendedContainer;
      ConstraintLayout recommendedContainer = ViewBindings.findChildViewById(rootView, id);
      if (recommendedContainer == null) {
        break missingId;
      }

      id = R.id.recommendedJobRecyclerView;
      RecyclerView recommendedJobRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recommendedJobRecyclerView == null) {
        break missingId;
      }

      id = R.id.topContainer;
      ConstraintLayout topContainer = ViewBindings.findChildViewById(rootView, id);
      if (topContainer == null) {
        break missingId;
      }

      id = R.id.txtRecommendedTitle;
      TextView txtRecommendedTitle = ViewBindings.findChildViewById(rootView, id);
      if (txtRecommendedTitle == null) {
        break missingId;
      }

      return new FragmentJobListingBinding((ConstraintLayout) rootView, btnAddJob, btnShowAllLatest,
          btnShowAllRecommended, container, lblLatestTitle, lblUserName, lblWelcome,
          nestedScrollView, newJobRecyclerView, newPostedContainer, noJobIndicator,
          overallContainer, profileImage, progressIndicator, recommendedContainer,
          recommendedJobRecyclerView, topContainer, txtRecommendedTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
