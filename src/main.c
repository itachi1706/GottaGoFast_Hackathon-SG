#include <pebble.h>
#include "prodlayout.h"

int main(void) {
  show_prodlayout();
  app_event_loop();
  hide_prodlayout();
}