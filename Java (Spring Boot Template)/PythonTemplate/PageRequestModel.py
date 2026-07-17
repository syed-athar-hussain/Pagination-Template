import base64
from typing import TypeVar, Generic, List, Optional, Any
from pydantic import BaseModel

T = TypeVar("T")

class PageRequest(BaseModel):
    limit: int = 20
    cursor: Optional[str] = None
    sort_by: str = "created_at"
    is_ascending: bool = True

class PageResult(BaseModel, Generic[T]):
    data: List[T]
    next_cursor: Optional[str]
    has_next: bool

class CursorData(BaseModel):
    sort_value: Any
    id: int