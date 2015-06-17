package io.nlopez.smartlocation;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.nlopez.smartlocation.location.LocationProvider;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import io.nlopez.smartlocation.location.utils.LocationState;
import java.util.HashMap;
import java.util.Map;

public class LocationControl {

  private static final Map<Context, LocationProvider> MAPPING = new HashMap<>();

  private final SmartLocation smartLocation;
  private LocationParams params;
  private LocationProvider provider;
  private boolean oneFix;

  public LocationControl(SmartLocation smartLocation) {
    this.smartLocation = smartLocation;
    params = LocationParams.BEST_EFFORT;
    oneFix = false;

    if (smartLocation.preInitialize) {
      if (!MAPPING.containsKey(smartLocation.context)) {
        MAPPING.put(smartLocation.context, new LocationGooglePlayServicesProvider());
      }
      provider = MAPPING.get(smartLocation.context);
      provider.init(smartLocation.context, smartLocation.logger);
    }
  }

  public LocationControl config(@NonNull LocationParams params) {
    this.params = params;
    return this;
  }

  public LocationControl provider(@NonNull LocationProvider newProvider) {
    if (provider != null && newProvider.getClass().equals(provider.getClass())) {
      smartLocation.logger.w("Creating a new provider that has the same class as before. Are you sure you want to do this?");
    }
    provider = newProvider;
    MAPPING.put(smartLocation.context, newProvider);
    provider.init(smartLocation.context, smartLocation.logger);
    return this;
  }

  public LocationControl oneFix() {
    this.oneFix = true;
    return this;
  }

  public LocationControl continuous() {
    this.oneFix = false;
    return this;
  }

  public LocationState state() {
    return LocationState.with(smartLocation.context);
  }

  @Nullable
  public Location getLastLocation() {
    return provider.getLastLocation();
  }

  public LocationControl get() {
    return this;
  }

  public void start(OnLocationUpdatedListener listener) {
    if (provider == null) {
      throw new RuntimeException("A provider must be initialized");
    }
    provider.start(listener, params, oneFix);
  }

  public void stop() {
    provider.stop();
  }

}
