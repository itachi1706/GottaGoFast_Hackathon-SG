#include <pebble.h>
#include "prodlayout.h"

// BEGIN AUTO-GENERATED UI CODE; DO NOT MODIFY
static Window *s_window;
static GBitmap *s_res_action_prev_white;
static GBitmap *s_res_action_next_white;
static GBitmap *s_res_action_tick_white;
static GFont s_res_gothic_24_bold;
static GFont s_res_gothic_14;
static GFont s_res_gothic_18_bold;
static ActionBarLayer *s_actionbarlayer_1;
static TextLayer *s_textlayer_title;
static TextLayer *s_textlayer_price;
static TextLayer *s_textlayer_avail;
static TextLayer *s_textlayer_locate;

static void initialise_ui(void) {
  s_window = window_create();
  #ifndef PBL_SDK_3
    window_set_fullscreen(s_window, false);
  #endif
  
  s_res_action_prev_white = gbitmap_create_with_resource(RESOURCE_ID_ACTION_PREV_WHITE);
  s_res_action_next_white = gbitmap_create_with_resource(RESOURCE_ID_ACTION_NEXT_WHITE);
  s_res_action_tick_white = gbitmap_create_with_resource(RESOURCE_ID_ACTION_TICK_WHITE);
  s_res_gothic_24_bold = fonts_get_system_font(FONT_KEY_GOTHIC_24_BOLD);
  s_res_gothic_14 = fonts_get_system_font(FONT_KEY_GOTHIC_14);
  s_res_gothic_18_bold = fonts_get_system_font(FONT_KEY_GOTHIC_18_BOLD);
  // s_actionbarlayer_1
  s_actionbarlayer_1 = action_bar_layer_create();
  action_bar_layer_add_to_window(s_actionbarlayer_1, s_window);
  action_bar_layer_set_background_color(s_actionbarlayer_1, GColorBlack);
  action_bar_layer_set_icon(s_actionbarlayer_1, BUTTON_ID_UP, s_res_action_prev_white);
  action_bar_layer_set_icon(s_actionbarlayer_1, BUTTON_ID_SELECT, s_res_action_tick_white);
  action_bar_layer_set_icon(s_actionbarlayer_1, BUTTON_ID_DOWN, s_res_action_next_white);
  layer_add_child(window_get_root_layer(s_window), (Layer *)s_actionbarlayer_1);
  
  // s_textlayer_title
  s_textlayer_title = text_layer_create(GRect(13, 19, 100, 24));
  text_layer_set_text(s_textlayer_title, "Product Title");
  text_layer_set_text_alignment(s_textlayer_title, GTextAlignmentCenter);
  text_layer_set_font(s_textlayer_title, s_res_gothic_24_bold);
  layer_add_child(window_get_root_layer(s_window), (Layer *)s_textlayer_title);
  
  // s_textlayer_price
  s_textlayer_price = text_layer_create(GRect(13, 53, 100, 24));
  text_layer_set_text(s_textlayer_price, "Product Price");
  text_layer_set_text_alignment(s_textlayer_price, GTextAlignmentCenter);
  text_layer_set_font(s_textlayer_price, s_res_gothic_18_bold);
  layer_add_child(window_get_root_layer(s_window), (Layer *)s_textlayer_price);
  
  // s_textlayer_avail
  s_textlayer_avail = text_layer_create(GRect(11, 104, 100, 20));
  text_layer_set_text(s_textlayer_avail, "Product Avail");
  text_layer_set_text_alignment(s_textlayer_avail, GTextAlignmentCenter);
  layer_add_child(window_get_root_layer(s_window), (Layer *)s_textlayer_avail);
  
  // s_textlayer_locate
  s_textlayer_locate = text_layer_create(GRect(22, 132, 100, 20));
  text_layer_set_text(s_textlayer_locate, "Prod Location");
  text_layer_set_text_alignment(s_textlayer_locate, GTextAlignmentRight);
  text_layer_set_font(s_textlayer_locate, s_res_gothic_14);
  layer_add_child(window_get_root_layer(s_window), (Layer *)s_textlayer_locate);
}

static void destroy_ui(void) {
  window_destroy(s_window);
  action_bar_layer_destroy(s_actionbarlayer_1);
  text_layer_destroy(s_textlayer_title);
  text_layer_destroy(s_textlayer_price);
  text_layer_destroy(s_textlayer_avail);
  text_layer_destroy(s_textlayer_locate);
  gbitmap_destroy(s_res_action_prev_white);
  gbitmap_destroy(s_res_action_next_white);
  gbitmap_destroy(s_res_action_tick_white);
}
// END AUTO-GENERATED UI CODE

bool debugMode = true;
static char avail_buffer[15];

enum {
  KEY_BUTTON_EVENT = 0,
  BUTTON_PREVIOUS = 1,
  BUTTON_NEXT = 2,
  BUTTON_REFRESH = 3,
  MESSAGE_DATA_EVENT = 4,
  MESSAGE_TITLE = 5,
  MESSAGE_PRICE = 6,
  MESSAGE_AVAIL = 7,
  MESSAGE_LOCATION = 8,
  MESSAGE_MAX_DATA = 9,
  MESSAGE_MIN_DATA = 10,
  ERROR_NO_DATA = 11,
};

/*         MESSAGE UPDATES            */
char *translate_error(AppMessageResult result) {
  switch (result) {
    case APP_MSG_OK: return "APP_MSG_OK";
    case APP_MSG_SEND_TIMEOUT: return "APP_MSG_SEND_TIMEOUT";
    case APP_MSG_SEND_REJECTED: return "APP_MSG_SEND_REJECTED";
    case APP_MSG_NOT_CONNECTED: return "APP_MSG_NOT_CONNECTED";
    case APP_MSG_APP_NOT_RUNNING: return "APP_MSG_APP_NOT_RUNNING";
    case APP_MSG_INVALID_ARGS: return "APP_MSG_INVALID_ARGS";
    case APP_MSG_BUSY: return "APP_MSG_BUSY";
    case APP_MSG_BUFFER_OVERFLOW: return "APP_MSG_BUFFER_OVERFLOW";
    case APP_MSG_ALREADY_RELEASED: return "APP_MSG_ALREADY_RELEASED";
    case APP_MSG_CALLBACK_ALREADY_REGISTERED: return "APP_MSG_CALLBACK_ALREADY_REGISTERED";
    case APP_MSG_CALLBACK_NOT_REGISTERED: return "APP_MSG_CALLBACK_NOT_REGISTERED";
    case APP_MSG_OUT_OF_MEMORY: return "APP_MSG_OUT_OF_MEMORY";
    case APP_MSG_CLOSED: return "APP_MSG_CLOSED";
    case APP_MSG_INTERNAL_ERROR: return "APP_MSG_INTERNAL_ERROR";
    default: return "UNKNOWN ERROR";
  }
}

static void inbox_dropped_callback(AppMessageResult reason, void *context) {
  if (debugMode)
    APP_LOG(APP_LOG_LEVEL_ERROR, "Message dropped! Reason: %i - %s", reason, translate_error(reason));
}

static void outbox_failed_callback(DictionaryIterator *iterator, AppMessageResult reason, void *context) {
  if (debugMode)
    APP_LOG(APP_LOG_LEVEL_ERROR, "Outbox send failed! Reason: %i - %s", reason, translate_error(reason));
  text_layer_set_text(s_textlayer_title, "Cannot Contact App Svc");
}

static void outbox_sent_callback(DictionaryIterator *iterator, void *context) {
  if (debugMode)
    APP_LOG(APP_LOG_LEVEL_INFO, "Outbox send success!");
}

//Updates Load Image (1 - Available, 0 - Not Avail)
static void updateAvail(int avail){
  switch (avail){
    case 0:
      snprintf(avail_buffer, sizeof(avail_buffer), "%s", "Available");
    case 1:
      snprintf(avail_buffer, sizeof(avail_buffer), "%s", "Not Available");
    break;
  }
}


// App Message API
static void in_received_handler(DictionaryIterator *iter, void *context){
  if (debugMode)
    APP_LOG(APP_LOG_LEVEL_INFO, "Message received!");
  // Get the first pair
  Tuple *t = dict_read_first(iter);
  // Long lived buffers
  static char title_buffer[60];
  static char price_buffer[6];
  static int availbility;
  static int max, current;
  static char store_buffer[10];
  static bool pgLoad = false, pgMaxLoad = false;

  // Process all pairs present
  while(t != NULL) {
    // Process this pair's key
    switch (t->key) {
      case MESSAGE_DATA_EVENT:
        APP_LOG(APP_LOG_LEVEL_INFO, "Data received for Dictionary %d", (int)t->value->int32);
        break;
      case MESSAGE_TITLE:
        snprintf(title_buffer, sizeof(title_buffer), "%s", t->value->cstring);
        text_layer_set_text(s_textlayer_title, title_buffer);
        break;
      case MESSAGE_PRICE:
        snprintf(price_buffer, sizeof(price_buffer), "%s", t->value->cstring);
        text_layer_set_text(s_textlayer_price, price_buffer);
        break;
      case MESSAGE_LOCATION:
        snprintf(store_buffer, sizeof(store_buffer), "%s", t->value->cstring);
        text_layer_set_text(s_textlayer_locate, store_buffer);
        break;
      case MESSAGE_AVAIL:
        availbility = t->value->int32;
        updateAvail(availbility);
        break;
      case MESSAGE_MAX_DATA:
        max = t->value->int32;
        pgMaxLoad = true;
        break;
      case MESSAGE_MIN_DATA:
        current = t->value->int32;
        pgLoad = true;
        break;
      //No Favourites? Tell them too
      case ERROR_NO_DATA:
        text_layer_set_text(s_textlayer_title, "No Favourites");
        break;
    }
    
    //Handle paging
    static char paging_buffer[10];
    if (pgLoad && pgMaxLoad){
      snprintf(paging_buffer, sizeof(paging_buffer), "%d/%d", current, max);
      text_layer_set_text(textlayer_pages, paging_buffer);
    }

    // Get next pair, if any
    t = dict_read_next(iter);
  }


static void handle_window_unload(Window* window) {
  destroy_ui();
}

void show_prodlayout(void) {
  initialise_ui();
  window_set_window_handlers(s_window, (WindowHandlers) {
    .unload = handle_window_unload,
  });
  window_stack_push(s_window, true);
}

void hide_prodlayout(void) {
  window_stack_remove(s_window, true);
}
