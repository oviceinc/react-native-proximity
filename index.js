import { NativeEventEmitter, NativeModules } from 'react-native';

const { RNProximity } = NativeModules;
const RNProximityEmitter = new NativeEventEmitter(RNProximity);

const addListener = () => {
  RNProximity.proximityEnabled(true);
  return RNProximityEmitter.addListener('ProximityStateDidChange');
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