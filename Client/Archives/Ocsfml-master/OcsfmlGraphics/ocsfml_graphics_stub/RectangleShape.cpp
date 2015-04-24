#include "RectangleShape.hpp"

#include "Vector.hpp"

#include "Shape.hpp"

#include <camlpp/custom_ops.hpp>



#define CAMLPP__CLASS_NAME() sf_RectangleShape
camlpp__register_preregistered_custom_operations( CAMLPP__DEFAULT_FINALIZE(), CAMLPP__NO_COMPARE(), CAMLPP__NO_HASH() )
camlpp__register_preregistered_custom_class()
{
  camlpp__register_inheritance_relationship( sf_Shape );
  camlpp__register_constructor0( default_constructor, 0);
  camlpp__register_constructor1( size_constructor, sf::Vector2f, 0);
  camlpp__register_method1( setSize, 0);
  camlpp__register_method0( getSize, 0);
}
#undef CAMLPP__CLASS_NAME
