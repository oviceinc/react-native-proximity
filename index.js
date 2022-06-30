import { NativeEventEmitter, NativeModules } from 'react-native';

const { RNProximity } = NativeModules;
const RNProximityEmitter = new NativeEventEmitter(RNProximity);

const addListener = (callback) => {
  RNProximity.proximityEnabled(true);
  return RNProximityEmitter.addListener('ProximityStateDidChange', callback);
};
const removeListener = (eventSubscription) => {
  RNProximity.proximityEnabled(false);
  eventSubscription.remove();
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