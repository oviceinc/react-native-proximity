import { NativeEventEmitter, NativeModules } from 'react-native';

const { RNProximity } = NativeModules;
const RNProximityEmitter = new NativeEventEmitter(RNProximity);

const addListener = (callback) => {
  RNProximity.proximityEnabled(true);
  RNProximityEmitter.addListener('ProximityStateDidChange', callback);
};
const removeListener = (callback) => {
  RNProximity.proximityEnabled(false);
  RNProximityEmitter.removeListener('ProximityStateDidChange', callback);
};

const removeAllListeners = () => {
  RNProximity.proximityEnabled(false);
  RNProximityEmitter.removeAllListeners('ProximityStateDidChange');
};

let campaign = null;

export default Proximity = {
  campaign: campaign,
  addListener : addListener,
  removeListener: removeListener,
  removeAllListeners: removeAllListeners
};