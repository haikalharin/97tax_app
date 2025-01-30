import 'js-snackbar/snackbar.css';
import { show, ACTION_TYPE } from 'js-snackbar';

export const SnackUtils = {
  warning: function (message) {
    show({
      text: message,
      pos: 'top-center',
      actionType: ACTION_TYPE.TEXT,
      // Color of the action text
      textColor: '#000',
      actionTextColor: '#000',

      // SnackBar background color
      backgroundColor: '#ffc107',
      actionText: 'Dismiss',
    });
  },
  message: function (message) {
    show({
      text: message,
      pos: 'top-center',
      actionType: ACTION_TYPE.TEXT,
      // Color of the action text
      textColor: '#ffffff',
      actionTextColor: '#ffffff',

      // SnackBar background color
      backgroundColor: '#323232',
      actionText: 'Dismiss',
    });
  },
  danger: function (message) {
    show({
      text: message,
      pos: 'top-center',
      actionType: ACTION_TYPE.TEXT,
      // Color of the action text
      textColor: '#ffffff',
      actionTextColor: '#ffffff',

      // SnackBar background color
      backgroundColor: '#dc3545',
      actionText: 'Dismiss',
    });
  }
};
