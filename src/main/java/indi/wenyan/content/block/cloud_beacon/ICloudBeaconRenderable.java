package indi.wenyan.content.block.cloud_beacon;

public interface ICloudBeaconRenderable {
    /// will show transmission in [-40, 40], require return continuous value in [-40, 40]
    int getTransmitAnimationTime();
}
