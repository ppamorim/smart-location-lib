package io.nlopez.smartlocation.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.location.DetectedActivity;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.activity.config.ActivityParams;
import io.nlopez.smartlocation.activity.providers.ActivityGooglePlayServicesProvider;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedro on 6/12/15.
 */
public class ActivityRecognitionControl {

  private static final Map<Context, ActivityProvider> MAPPING = new HashMap<>();

  private final SmartLocation smartLocation;
  private ActivityParams params;
  private ActivityProvider provider;

  public ActivityRecognitionControl(SmartLocation smartLocation) {
    this.smartLocation = smartLocation;
    params = ActivityParams.NORMAL;
    if (smartLocation.preInitialize) {
      if (!MAPPING.containsKey(smartLocation.context)) {
        MAPPING.put(smartLocation.context, new ActivityGooglePlayServicesProvider());
      }
      provider = MAPPING.get(smartLocation.context);

      provider.init(smartLocation.context, smartLocation.logger);
    }
  }

  public ActivityRecognitionControl config(@NonNull ActivityParams params) {
    this.params = params;
    return this;
  }

  public ActivityRecognitionControl provider(@NonNull ActivityProvider newProvider) {
    if (provider != null && newProvider.getClass().equals(provider.getClass())) {
      smartLocation.logger.w("Creating a new provider that has the same class as before. Are you sure you want to do this?");
    }
    provider = newProvider;
    MAPPING.put(smartLocation.context, newProvider);
    provider.init(smartLocation.context, smartLocation.logger);
    return this;
  }

  @Nullable
  public DetectedActivity getLastActivity() {
    return provider.getLastActivity();
  }

  public ActivityRecognitionControl get() {
    return this;
  }

  public void start(OnActivityUpdatedListener listener) {
    if (provider == null) {
      throw new RuntimeException("A provider must be initialized");
    }
    provider.start(listener, params);
  }

  public void stop() {
    provider.stop();
  }

}
